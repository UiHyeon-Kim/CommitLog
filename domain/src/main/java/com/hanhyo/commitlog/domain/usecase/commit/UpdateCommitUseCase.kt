package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class UpdateCommitUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(commit: Commit): Result<Unit> {
        return try {
            if (!commit.id.isValid()) {
                return Result.error(DomainError.ValidationError("유효하지 않은 커밋 ID입니다"))
            }

            commitRepository.updateCommit(commit)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.error(DomainError.DatabaseError("커밋 수정 실패", e))
        }
    }
}
