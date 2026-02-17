package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import java.time.LocalDate
import javax.inject.Inject

class GetCommitsByDateRangeUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Result<List<Commit>> = runSuspendCatching {
        require(!startDate.isAfter(endDate)) { "시작 날짜는 종료 날짜보다 이전이어야 합니다" }
        commitRepository.getCommitsByDateRange(startDate, endDate)
    }
}
