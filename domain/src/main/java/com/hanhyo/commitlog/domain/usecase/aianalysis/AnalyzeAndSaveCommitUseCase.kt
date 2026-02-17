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
        // AI 분석 시도
        val analyzedCommit = try {
             val analysisResult = aiRepository.analyzeCommit(
                title = commit.title,
                learnedToday = commit.learnedToday,
                difficulties = commit.difficulties,
                tomorrowPlan = commit.tomorrowPlan,
            )
            commit.withAnalysis(
                analysis = analysisResult.analysis,
                tags = analysisResult.tags
            )
        } catch (e: Exception) {
            // AI 분석 실패 시 로그 남기거나 무시하고 원본 커밋 사용
            // 실제 프로덕션에서는 Logger 사용 권장
            commit
        }

        return try {
            val id = commitRepository.saveCommit(analyzedCommit)
            Result.success(analyzedCommit.copy(id = CommitId(id)))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.error(DomainError.DatabaseError("커밋 저장 실패", e))
        }
    }
}
