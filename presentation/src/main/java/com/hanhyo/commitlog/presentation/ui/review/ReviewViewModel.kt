package com.hanhyo.commitlog.presentation.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.domain.usecase.aianalysis.GenerateMonthlyReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val generateMonthlyReviewUseCase: GenerateMonthlyReviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ReviewEffect>(replay = 0)
    val effect: SharedFlow<ReviewEffect> = _effect.asSharedFlow()

    init {
        val now = LocalDate.now()
        _uiState.update {
            it.copy(
                selectedYear = now.year,
                selectedMonth = now.monthValue,
            )
        }
    }

    fun generateReview() {
        val state = _uiState.value
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = generateMonthlyReviewUseCase(state.selectedYear, state.selectedMonth)) {
                is Result.Success -> {
                    val review = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            review = review,
                            errorMessage = null,
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.message,
                        )
                    }
                    _effect.emit(ReviewEffect.ShowError(result.error.message))
                }

                is Result.Loading -> {}
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
    }

    fun goToPreviousMonth() {
        val state = _uiState.value
        val (newYear, newMonth) = if (state.selectedMonth == 1) {
            state.selectedYear - 1 to 12
        } else {
            state.selectedYear to state.selectedMonth - 1
        }
        updateMonth(newYear, newMonth)
    }

    fun goToNextMonth() {
        val state = _uiState.value
        val (newYear, newMonth) = if (state.selectedMonth == 12) {
            state.selectedYear + 1 to 1
        } else {
            state.selectedYear to state.selectedMonth + 1
        }
        updateMonth(newYear, newMonth)
    }
}

data class ReviewUiState(
    val isLoading: Boolean = false,
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 1,
    val review: MonthlyReview? = null,
    val errorMessage: String? = null,
)

sealed interface ReviewEffect {
    data class ShowError(val message: String) : ReviewEffect
}
