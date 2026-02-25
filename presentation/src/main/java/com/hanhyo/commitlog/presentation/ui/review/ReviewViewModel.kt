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
        // 수동 생성 및 정기 생성 태그 모두 관찰
        val tags = listOf("monthly_review_manual", "monthly_review_regular")
        
        viewModelScope.launch {
            // 여러 태그를 합쳐서 관찰하는 로직 (단순화를 위해 각 태그별로 Flow를 병합하거나 
            // 여기서는 수동 생성("monthly_review_manual")을 우선시하여 관찰합니다.)
            workManager.getWorkInfosByTagFlow("monthly_review_manual")
                .onEach { workInfos ->
                    // 1. 가장 최근의 작업 순으로 정렬 (결과 결정론성 확보)
                    val sortedWorks = workInfos.sortedByDescending { it.nextScheduleTimeMillis } 
                    // Note: OneTimeWorkRequest의 경우 stopTime이나 id 등으로 정렬할 수 있으나, 
                    // state 변화를 추적하기 위해 전체 리스트에서 활성 작업을 찾습니다.
                    
                    val activeWork = workInfos.firstOrNull { !it.state.isFinished }
                    val lastWork = workInfos.maxByOrNull { 
                        if (it.state.isFinished) it.id.mostSignificantBits else Long.MIN_VALUE 
                    }

                    val errorMsg = if (lastWork?.state == WorkInfo.State.FAILED) {
                        lastWork.outputData.getString("error_message") ?: "회고 생성에 실패했습니다."
                    } else {
                        null
                    }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = activeWork != null,
                            errorMessage = errorMsg ?: state.errorMessage
                        )
                    }
                }
                .launchIn(this)
        }
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
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
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
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 1,
    val availableMonths: List<YearMonth> = emptyList(),
    val monthCommitCount: Int = 0,
    val review: MonthlyReview? = null,
    val errorMessage: String? = null,
)

sealed interface ReviewEffect {
    data class ShowError(val message: String) : ReviewEffect
}
