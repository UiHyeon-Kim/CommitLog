package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * 오늘 날짜의 최신 커밋 하나를 가져오는 UseCase
 */
class GetTodayCommitUseCase @Inject constructor(
    private val repository: CommitRepository
) {
    suspend operator fun invoke(): Result<Commit?> {
        return runCatching {
            val today = LocalDate.now(java.time.ZoneId.systemDefault())
            // 오늘 날짜의 커밋들을 가져와서 가장 최근 것(목록의 첫 번째)을 반환
            repository.getCommitsByDateRange(today, today).firstOrNull()
        }
    }
}
