package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class SaveCommitUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(commit: Commit): Result<Long> {
        return try {
            val id = commitRepository.saveCommit(commit)
            Result.success(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.error(DomainError.DatabaseError("커밋 저장 실패", e))
        }
    }
}
