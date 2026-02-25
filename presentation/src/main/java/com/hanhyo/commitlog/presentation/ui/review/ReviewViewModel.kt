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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _effect = MutableSharedFlow<ReviewEffect>(replay = 0)
    val effect: SharedFlow<ReviewEffect> = _effect.asSharedFlow()

    private var allCommits: List<Commit> = emptyList()

    init {
        observeCommits()
        observeWorkManager()
        observeMonthlyReview()
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

        combine(manualFlow, regularFlow) { manualWorks, regularWorks ->
            manualWorks + regularWorks
        }
            .distinctUntilChanged()
            .onEach { workInfos ->
                // 가장 최근의 작업 순으로 정렬
                val sortedWorks = workInfos.sortedByDescending { it.nextScheduleTimeMillis }

                val activeWork = workInfos.firstOrNull { !it.state.isFinished }
                val lastFinishedWork = workInfos
                    .filter { it.state.isFinished }
                    .maxByOrNull { it.id.mostSignificantBits } // 임시 정렬 기준 (실제로는 stopTime이 더 정확)

                val errorMsg = if (lastFinishedWork?.state == WorkInfo.State.FAILED) {
                    lastFinishedWork.outputData.getString("error_message")
                } else {
                    null
                }

                _uiState.update { state ->
                    state.copy(
                        isLoading = activeWork != null,
                        // 작업이 성공적으로 끝났다면 이전 에러 메시지를 초기화, 실패했다면 새 에러 메시지 표시
                        errorMessage = when {
                            activeWork != null -> null // 작업 중에는 에러 메시지 숨김
                            lastFinishedWork?.state == WorkInfo.State.SUCCEEDED -> null
                            else -> errorMsg ?: state.errorMessage
                        }
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                scheduleMonthlyReviewUseCase(state.selectedYear, state.selectedMonth)
            } catch (e: CancellationException) {
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

    fun goToPreviousMonth() {
        val state = _uiState.value

        val currentIndex = state.availableMonths.indexOfFirst {
            it.year == state.selectedYear && it.monthValue == state.selectedMonth
        }

        if (currentIndex != -1 && currentIndex < state.availableMonths.lastIndex) {
            val prev = state.availableMonths[currentIndex + 1]
            updateMonth(prev.year, prev.monthValue)
        }
    }

    fun goToNextMonth() {
        val state = _uiState.value
        val currentIndex = state.availableMonths.indexOfFirst {
            it.year == state.selectedYear && it.monthValue == state.selectedMonth
        }

        if (currentIndex > 0) {
            val next = state.availableMonths[currentIndex - 1]
            updateMonth(next.year, next.monthValue)
        }
    }
}

data class ReviewUiState(
    val isLoading: Boolean = false,
    val selectedYear: Int = LocalDate.now().year,
    val selectedMonth: Int = LocalDate.now().monthValue,
    val availableMonths: List<YearMonth> = emptyList(),
    val monthCommitCount: Int = 0,
    val review: MonthlyReview? = null,
    val errorMessage: String? = null,
)

sealed interface ReviewEffect {
    data class ShowError(val message: String) : ReviewEffect
}
