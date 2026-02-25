package com.hanhyo.commitlog.presentation.ui.main

import androidx.lifecycle.ViewModel
import com.hanhyo.commitlog.domain.usecase.aianalysis.ScheduleRegularMonthlyReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val scheduleRegularMonthlyReviewUseCase: ScheduleRegularMonthlyReviewUseCase
) : ViewModel() {

    fun scheduleMonthlyReview() {
        scheduleRegularMonthlyReviewUseCase()
    }
}
