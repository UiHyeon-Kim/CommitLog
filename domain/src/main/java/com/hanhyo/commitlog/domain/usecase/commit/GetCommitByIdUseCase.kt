package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

class GetCommitByIdUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(id: CommitId): Result<Commit> = runSuspendCatching {
        commitRepository.getCommitById(id)
            ?: throw NoSuchElementException("커밋을 찾을 수 없습니다")
    }
}
