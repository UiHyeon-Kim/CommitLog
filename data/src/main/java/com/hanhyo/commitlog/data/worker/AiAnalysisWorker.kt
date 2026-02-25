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
import kotlinx.coroutines.CancellationException
import timber.log.Timber

@HiltWorker
class AiAnalysisWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val commitRepository: CommitRepository,
    private val aiAnalysisRepository: AiAnalysisRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val commitId = inputData.getLong(WorkerConstants.KEY_COMMIT_ID, -1)
        if (commitId == -1L) return Result.failure()

        return try {
            val commitForAnalysis = commitRepository.getCommitById(CommitId(commitId))
                ?: return Result.failure()

            // AI 분석 요청
            val result = aiAnalysisRepository.analyzeCommit(
                title = commitForAnalysis.title,
                learnedToday = commitForAnalysis.learnedToday,
                difficulties = commitForAnalysis.difficulties,
                tomorrowPlan = commitForAnalysis.tomorrowPlan
            )

            // 분석 수행 중 사용자가 커밋 내용을 수정했을 수 있으므로 최신 커밋 정보를 다시 가져옴
            val latestCommit = commitRepository.getCommitById(CommitId(commitId))
                ?: return Result.failure()

            // AI 분석 응답 파싱 실패(기본값 반환)인 경우, 실패로 기록하고 무한 재시도 방지
            if (result.analysis.comment == "분석 결과를 불러올 수 없습니다") {
                commitRepository.updateCommit(latestCommit.withAnalysisFailed())
                return Result.failure()
            }

            // 분석 결과 저장 및 상태 완료
            commitRepository.updateCommit(
                latestCommit.withAnalysis(
                    analysis = result.analysis,
                    tags = result.tags // AI가 분석한 태그 사용
                )
            )

            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "AI Analysis failed for commit $commitId")
            
            // 실패 상태로 업데이트
            val commit = commitRepository.getCommitById(CommitId(commitId))
            commit?.let {
                commitRepository.updateCommit(it.withAnalysisFailed())
            }
            
            if (runAttemptCount >= MAX_RETRY_COUNT) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }

    companion object {
        const val MAX_RETRY_COUNT = 3
    }
}
