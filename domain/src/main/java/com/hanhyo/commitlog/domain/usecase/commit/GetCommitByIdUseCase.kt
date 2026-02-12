package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class GetCommitByIdUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(id: CommitId): Result<Commit> {
        return try {
            val commit = commitRepository.getCommitById(id)

            if (commit != null) {
                Result.success(commit)
            } else {
                Result.error(DomainError.NotFoundError("커밋을 찾을 수 없습니다"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.error(DomainError.DatabaseError("커밋 조회 실패", e))
        }
    }
}
