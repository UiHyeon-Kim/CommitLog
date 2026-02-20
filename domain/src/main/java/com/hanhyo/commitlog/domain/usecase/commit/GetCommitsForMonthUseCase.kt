package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 특정 월의 모든 커밋 조회 UseCase
 *
 * - 주어진 연도와 월에 해당하는 모든 커밋 목록을 데이터베이스에서 가져옵니다.
 */
class GetCommitsForMonthUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<List<Commit>> = runSuspendCatching {
        require(month in 1..12) { "월은 1-12 범위여야 합니다" }
        commitRepository.getCommitsForMonth(year, month)
    }
}
