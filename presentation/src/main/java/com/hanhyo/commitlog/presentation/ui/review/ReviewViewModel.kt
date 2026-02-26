package com.hanhyo.commitlog.presentation.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.domain.usecase.aianalysis.ObserveMonthlyReviewUseCase
import com.hanhyo.commitlog.domain.usecase.aianalysis.ScheduleMonthlyReviewUseCase
import com.hanhyo.commitlog.domain.usecase.commit.ObserveAllCommitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val observeMonthlyReviewUseCase: ObserveMonthlyReviewUseCase,
    private val scheduleMonthlyReviewUseCase: ScheduleMonthlyReviewUseCase,
    private val observeAllCommitsUseCase: ObserveAllCommitsUseCase,
    private val workManager: WorkManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var allCommits: List<Commit> = emptyList()

    init {
        observeCommits()
        observeWorkManager()
        observeMonthlyReview()
        val today = LocalDate.now()
        setYearMonth(today.year, today.monthValue)
    }

    /**
     * 선택된 연/월에 해당하는 회고 데이터를 관찰하여 UI 상태를 자동 업데이트합니다.
     */
    private fun observeMonthlyReview() {
        _uiState
            .map { it.selectedYear to it.selectedMonth }
            .distinctUntilChanged()
            .flatMapLatest { (year, month) ->
                observeMonthlyReviewUseCase(year, month)
            }
            .onEach { review ->
                _uiState.update { it.copy(review = review) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * WorkManager의 상태를 관찰하여 백그라운드에서 진행 중인 회고 생성 상태를 UI에 반영합니다.
     */
    private fun observeWorkManager() {
        val manualFlow = workManager.getWorkInfosByTagFlow("monthly_review_manual")
        val regularFlow = workManager.getWorkInfosByTagFlow("monthly_review_regular")

        // 수동 및 정기 생성 태그 모두 관찰하여 병합
        combine(manualFlow, regularFlow) { manual, regular ->
            manual + regular
        }
            .onEach { workInfos ->
                // 활성 작업 찾기
                // 정기 작업(regular)의 ENQUEUED는 대기 상태이므로 로딩으로 치지 않음
                // '실행 중(RUNNING)'이거나, '수동(manual)으로 방금 넣은 ENQUEUED'만 로딩으로 간주
                val activeWork = workInfos.firstOrNull {
                    it.state == WorkInfo.State.RUNNING ||
                            (it.tags.contains("monthly_review_manual") && it.state == WorkInfo.State.ENQUEUED)
                }

                // 완료된 작업만 필터링 후, 타임스탬프(finished_at) 기준으로 가장 최근 작업 추출
                val finishedWorks = workInfos.filter { it.state.isFinished }
                val lastFinishedWork = finishedWorks.maxByOrNull {
                    // Worker가 outputData에 넣어준 시간을 기준점으로 사용
                    it.outputData.getLong("finished_at", 0L)
                }

                // 에러 메시지 처리 (활성 작업이 없고, 가장 최근 종료된 작업이 실패했을 때만)
                val errorMsg = if (activeWork == null && lastFinishedWork?.state == WorkInfo.State.FAILED) {
                    lastFinishedWork.outputData.getString("error_message") ?: "회고 생성에 실패했습니다."
                } else {
                    null
                }

                _uiState.update { state ->
                    state.copy(
                        isLoading = activeWork != null,
                        errorMessage = errorMsg
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeCommits() {
        observeAllCommitsUseCase()
            .onEach { commits ->
                allCommits = commits
                val months = commits.map { YearMonth.from(it.date) }
                    .distinct()
                    .sortedDescending()

                _uiState.update { state ->
                    var newYear = state.selectedYear
                    var newMonth = state.selectedMonth

                    if (months.isNotEmpty()) {
                        val current = YearMonth.of(state.selectedYear, state.selectedMonth)
                        if (current !in months) {
                            newYear = months.first().year
                            newMonth = months.first().monthValue
                        }
                    }

                    state.copy(
                        availableMonths = months,
                        selectedYear = newYear,
                        selectedMonth = newMonth
                    )
                }
                updateCommitCount()
            }
            .launchIn(viewModelScope)
    }

    private fun setYearMonth(year: Int, month: Int) {
        _uiState.update { it.copy(selectedYear = year, selectedMonth = month) }
    }

    /**
     * 월간 회고 생성을 시작합니다.
     *
     * - ViewModelScope 대신 WorkManager를 사용하여 앱이 백그라운드로 나가도 작업이 유지되도록 합니다.
     * - 작업 완료 후 알림 발송 로직은 GenerateMonthlyReviewWorker 내부에 포함되어 있습니다.
     */
    fun generateReview() {
        val state = _uiState.value
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, review = null) }
            try {
                scheduleMonthlyReviewUseCase(state.selectedYear, state.selectedMonth)
            } catch (e: CancellationException) {
                _uiState.update { it.copy(isLoading = false) }
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "요청 중 오류가 발생했습니다.") }
            }
        }
    }

    fun updateMonth(year: Int, month: Int) {
        _uiState.update {
            it.copy(
                selectedYear = year,
                selectedMonth = month,
                review = null,
                errorMessage = null,
            )
        }
        updateCommitCount()
    }

    private fun updateCommitCount() {
        val state = _uiState.value
        val count = allCommits.count {
            it.date.year == state.selectedYear && it.date.monthValue == state.selectedMonth
        }
        _uiState.update { it.copy(monthCommitCount = count) }
    }
}

data class ReviewUiState(
    val isLoading: Boolean = true,
    val selectedYear: Int = LocalDate.now().year,
    val selectedMonth: Int = LocalDate.now().monthValue,
    val availableMonths: List<YearMonth> = emptyList(),
    val monthCommitCount: Int = 0,
    val review: MonthlyReview? = null,
    val errorMessage: String? = null,
)
