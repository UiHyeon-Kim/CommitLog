package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 월간 회고 생성 UseCase
 *
 * - 특정 연도와 월에 해당하는 커밋들을 조회하여, AI를 통해 월간 회고 리포트를 생성합니다.
 * - 임시 저장된 커밋은 제외하며, 해당 월에 커밋이 없으면 예외를 발생시킵니다.
 */
class GenerateMonthlyReviewUseCase @Inject constructor(
    private val aiRepository: AiAnalysisRepository,
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<MonthlyReview> = runSuspendCatching {
        require(month in 1..12) { "월은 1-12 범위여야 합니다" }

        val commits = commitRepository.getCommitsForMonth(year, month)
            .filter { !it.isDraft }

        check(commits.isNotEmpty()) { "${year}년 ${month}월에 작성된 커밋이 없습니다" }

        aiRepository.generateMonthlyReview(commits, year, month)
    }
}
