package com.hanhyo.commitlog.presentation.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.domain.usecase.aianalysis.GenerateMonthlyReviewUseCase
import com.hanhyo.commitlog.domain.usecase.commit.ObserveAllCommitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val generateMonthlyReviewUseCase: GenerateMonthlyReviewUseCase,
    private val observeAllCommitsUseCase: ObserveAllCommitsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ReviewEffect>(replay = 0)
    val effect: SharedFlow<ReviewEffect> = _effect.asSharedFlow()

    private var allCommits: List<Commit> = emptyList()

    init {
        observeCommits()
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

    fun generateReview() {
        val state = _uiState.value
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            generateMonthlyReviewUseCase(state.selectedYear, state.selectedMonth)
                .onSuccess { review ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            review = review,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message,
                        )
                    }
                    _effect.emit(ReviewEffect.ShowError(error.message ?: "회고 생성 실패"))
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
