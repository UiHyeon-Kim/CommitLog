package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class GetCommitsForMonthUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<List<Commit>> {
        return try {
            if (month !in 1..12) {
                return Result.error(DomainError.ValidationError("월은 1-12 범위여야 합니다"))
            }

            val commits = commitRepository.getCommitsForMonth(year, month)
            Result.success(commits)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.error(DomainError.DatabaseError("커밋 조회 실패", e))
        }
    }
}
