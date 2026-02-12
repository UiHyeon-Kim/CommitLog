package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

class DeleteCommitUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(commit: Commit): Result<Unit> {
        return try {
            commitRepository.deleteCommit(commit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error(DomainError.DatabaseError("커밋 삭제 실패", e))
        }
    }
}
