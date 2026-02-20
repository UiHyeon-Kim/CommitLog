package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 커밋 삭제 UseCase
 *
 * - 특정 커밋을 데이터베이스에서 삭제합니다.
 */
class DeleteCommitUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(commit: Commit): Result<Unit> = runSuspendCatching {
        commitRepository.deleteCommit(commit)
    }
}
