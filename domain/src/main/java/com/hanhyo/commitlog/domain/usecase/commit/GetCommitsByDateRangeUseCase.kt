package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import javax.inject.Inject

class GetCommitsByDateRangeUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): Result<List<Commit>> {
        return try {
            if (startDate.isAfter(endDate)) {
                return Result.error(DomainError.ValidationError("시작 날짜는 종료 날짜보다 이전이어야 합니다"))
            }

            val commits = commitRepository.getCommitsByDateRange(startDate, endDate)
            Result.success(commits)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.error(DomainError.DatabaseError("커밋 조회 실패", e))
        }
    }
}
