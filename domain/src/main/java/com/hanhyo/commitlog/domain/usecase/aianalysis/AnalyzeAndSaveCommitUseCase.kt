package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 커밋 분석 및 저장 UseCase
 *
 * - 커밋을 저장하기 전, AI 분석 상태를 '대기중(PENDING)'으로 설정하고 데이터베이스에 저장합니다.
 * - 저장이 완료되면 백그라운드에서 AI 분석을 수행하도록 `ScheduleAnalysisUseCase`를 호출합니다.
 */
class AnalyzeAndSaveCommitUseCase @Inject constructor(
    private val commitRepository: CommitRepository,
    private val scheduleAnalysisUseCase: ScheduleAnalysisUseCase
) {
    suspend operator fun invoke(commit: Commit): Result<Long> = runSuspendCatching {
        // 분석 상태를 Pending으로 설정하고 저장
        val commitWithPending = commit.withAnalysisPending()
        val savedId = commitRepository.saveCommit(commitWithPending)

        // WorkManager로 분석 예약
        scheduleAnalysisUseCase(savedId)
            .onFailure { cause ->
                // 스케줄링 실패 시 커밋 상태를 FAILED로 표시하고 예외 전파 (고아 데이터 방지)
                val failedCommit = commitWithPending.copy(id = CommitId(savedId)).withAnalysisFailed()
                commitRepository.updateCommit(failedCommit)
                throw cause
            }
            .getOrThrow()
        
        savedId
    }
}
