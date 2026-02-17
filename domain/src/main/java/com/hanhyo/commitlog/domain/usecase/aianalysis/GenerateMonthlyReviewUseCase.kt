package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

class GenerateMonthlyReviewUseCase @Inject constructor(
    private val aiRepository: AiAnalysisRepository,
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<MonthlyReview> = runSuspendCatching {
        require(month in 1..12) { "월은 1-12 범위여야 합니다" }

        val commits = commitRepository.getCommitsForMonth(year, month)
            .filter { !it.isDraft }

        check(commits.isNotEmpty()) { "작성된 커밋이 없습니다" }

        aiRepository.generateMonthlyReview(commits, year, month)
    }
}
