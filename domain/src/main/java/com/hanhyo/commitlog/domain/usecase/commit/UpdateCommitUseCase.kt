package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 커밋 수정 UseCase
 *
 * - 기존 커밋의 내용을 업데이트합니다. 반드시 유효한 커밋 ID가 있어야 합니다.
 */
class UpdateCommitUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(commit: Commit): Result<Unit> = runSuspendCatching {
        require(commit.id.isValid()) { "유효하지 않은 커밋 ID입니다" }
        commitRepository.updateCommit(commit)
    }
}
