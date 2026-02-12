package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class GenerateMonthlyReviewUseCase @Inject constructor(
    private val aiRepository: AiAnalysisRepository,
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<MonthlyReview> {
        return try {
            if (month !in 1..12) {
                return Result.error(
                    DomainError.ValidationError("월은 1-12 범위여야 합니다")
                )
            }

            // 해당 월의 커밋 조회
            val commits = commitRepository.getCommitsForMonth(year, month)
                .filter { !it.isDraft }

            if (commits.isEmpty()) {
                return Result.error(
                    DomainError.ValidationError("작성된 커밋이 없습니다")
                )
            }

            // AI 회고 생성
            val review = aiRepository.generateMonthlyReview(commits, year, month)
            Result.success(review)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.error(DomainError.AiAnalysisError("회고 생성 실패: ${e.message}", e))
        }
    }
}
