package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import javax.inject.Inject

/**
 * 월간 회고 생성을 백그라운드 작업으로 예약하는 UseCase
 */
class ScheduleMonthlyReviewUseCase @Inject constructor(
    private val aiRepository: AiAnalysisRepository
) {
    suspend operator fun invoke(year: Int, month: Int) {
        aiRepository.scheduleMonthlyReview(year, month)
    }
}
