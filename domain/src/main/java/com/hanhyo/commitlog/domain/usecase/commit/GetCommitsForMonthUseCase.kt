package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

class GetCommitsForMonthUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<List<Commit>> = runSuspendCatching {
        require(month in 1..12) { "월은 1-12 범위여야 합니다" }
        commitRepository.getCommitsForMonth(year, month)
    }
}
