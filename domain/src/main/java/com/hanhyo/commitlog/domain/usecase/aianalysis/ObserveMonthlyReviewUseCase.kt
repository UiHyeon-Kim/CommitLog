package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 특정 월의 회고 데이터를 관찰하는 UseCase
 */
class ObserveMonthlyReviewUseCase @Inject constructor(
    private val aiRepository: AiAnalysisRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<MonthlyReview?> {
        return aiRepository.observeMonthlyReview(year, month)
    }
}
