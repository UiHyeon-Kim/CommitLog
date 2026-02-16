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
                // 분석 실패 시 로그 남기고 분석 없는 상태로 진행
                // Timber.e(e, "AI 분석 실패, 분석 없이 저장 진행")
                commit
            }

            try {
                val id = commitRepository.saveCommit(analyzedCommit)
                Result.success(analyzedCommit.copy(id = CommitId(id)))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.error(DomainError.DatabaseError("커밋 저장 실패", e))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IllegalArgumentException) {
            Result.error(DomainError.ValidationError(e.message ?: "유효하지 않은 입력"))
        } catch (e: Exception) {
            // 여기로 오면 정말 예상치 못한 에러
            Result.error(DomainError.Unknown("알 수 없는 오류: ${e.message}"))
        }
    }
}
