package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Streak
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

class GetStreakUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(): Result<Streak> = runSuspendCatching {
        commitRepository.calculateStreak()
    }
}
