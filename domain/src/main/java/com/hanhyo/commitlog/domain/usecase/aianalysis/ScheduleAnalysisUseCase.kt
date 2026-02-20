package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import javax.inject.Inject

/**
 * AI 분석 예약 UseCase
 *
 * -  특정 커밋에 대한 AI 분석 작업을 백그라운드에서 실행하도록 예약합니다. WorkManager를 통해 처리됩니다.
 */
class ScheduleAnalysisUseCase @Inject constructor(
    private val aiAnalysisRepository: AiAnalysisRepository
) {
    suspend operator fun invoke(commitId: Long): Result<Unit> = runSuspendCatching {
        aiAnalysisRepository.scheduleAnalysis(commitId)
    }
}
