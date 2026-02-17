package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

class GetTotalCommitCountUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(): Result<Int> = runSuspendCatching {
        commitRepository.getTotalCommitCount()
    }
}
