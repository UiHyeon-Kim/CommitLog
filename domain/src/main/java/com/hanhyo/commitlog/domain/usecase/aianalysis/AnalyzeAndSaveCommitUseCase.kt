package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class AnalyzeAndSaveCommitUseCase @Inject constructor(
    private val aiRepository: AiAnalysisRepository,
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(commit: Commit): Result<Commit> {
        return try {
            // AI 분석
            val analysisResult = aiRepository.analyzeCommit(
                title = commit.title,
                learnedToday = commit.learnedToday,
                difficulties = commit.difficulties,
                tomorrowPlan = commit.tomorrowPlan,
            )

            // 분석 결과 삽입
            val analyzedCommit = commit.withAnalysis(
                analysis = analysisResult.analysis,
                tags = analysisResult.tags
            )

            try {
                val id = commitRepository.saveCommit(analyzedCommit)
                Result.success(analyzedCommit.copy(id = CommitId(id)))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.error(DomainError.DatabaseError("분석된 커밋 저장 실패", e))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IllegalArgumentException) {
            Result.error(DomainError.ValidationError(e.message ?: "유효하지 않은 입력"))
        } catch (e: Exception) {
            Result.error(DomainError.AiAnalysisError("AI 분석 실패: ${e.message}", e))
        }
    }
}
