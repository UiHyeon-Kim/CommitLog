package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * 특정 날짜 범위의 모든 커밋 조회 UseCase
 *
 * - 주어진 시작 날짜와 종료 날짜 사이의 모든 커밋 목록을 데이터베이스에서 가져옵니다.
 */
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
