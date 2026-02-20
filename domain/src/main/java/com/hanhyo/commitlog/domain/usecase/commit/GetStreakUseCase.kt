package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Streak
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 연속 기록(Streak) 조회 UseCase
 *
 * - 현재까지의 연속 기록 일수를 계산하고, 오늘 기록을 남겼는지 여부를 확인합니다.
 */
class GetStreakUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(): Result<Streak> = runSuspendCatching {
        commitRepository.calculateStreak()
    }
}
