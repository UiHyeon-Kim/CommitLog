package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.scheduler.ReviewScheduler
import javax.inject.Inject

/**
 * 정기적인 월간 회고 알림 및 분석 작업을 예약하는 UseCase
 */
class ScheduleRegularMonthlyReviewUseCase @Inject constructor(
    private val reviewScheduler: ReviewScheduler
) {
    operator fun invoke() {
        reviewScheduler.scheduleRegularMonthlyReview()
    }
}
