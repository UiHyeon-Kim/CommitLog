package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 커밋 저장 UseCase
 *
 * - 새로운 커밋 또는 임시 저장된 커밋을 데이터베이스에 저장합니다.
 */
class SaveCommitUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(commit: Commit): Result<Long> = runSuspendCatching {
        commitRepository.saveCommit(commit)
    }
}
