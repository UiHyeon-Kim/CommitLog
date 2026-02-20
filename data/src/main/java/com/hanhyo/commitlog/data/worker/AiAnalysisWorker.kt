package com.hanhyo.commitlog.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class AiAnalysisWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val commitRepository: CommitRepository,
    private val aiAnalysisRepository: AiAnalysisRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val commitId = inputData.getLong(KEY_COMMIT_ID, -1)
        if (commitId == -1L) return Result.failure()

        return try {
            val commit = commitRepository.getCommitById(CommitId(commitId))
                ?: return Result.failure()

            // 이미 분석되었거나 완료된 상태라면 스킵 (하지만 재분석 요청일 수도 있음)
            // 여기서는 항상 재분석 수행

            // 상태 업데이트: PENDING (이미 WriteViewModel에서 설정했겠지만, 안전장치)
            commitRepository.updateCommit(commit.withAnalysisPending())

            // AI 분석 요청
            val result = aiAnalysisRepository.analyzeCommit(
                title = commit.title,
                learnedToday = commit.learnedToday,
                difficulties = commit.difficulties,
                tomorrowPlan = commit.tomorrowPlan
            )

            // 분석 결과 저장 및 상태 완료
            commitRepository.updateCommit(
                commit.withAnalysis(
                    analysis = result.analysis,
                    tags = result.tags // AI가 분석한 태그 사용
                )
            )

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "AI Analysis failed for commit $commitId")
            
            // 실패 상태로 업데이트
            val commit = commitRepository.getCommitById(CommitId(commitId))
            commit?.let {
                commitRepository.updateCommit(it.withAnalysisFailed())
            }
            
            if (runAttemptCount >= 3) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }

    companion object {
        const val KEY_COMMIT_ID = "commit_id"
    }
}
