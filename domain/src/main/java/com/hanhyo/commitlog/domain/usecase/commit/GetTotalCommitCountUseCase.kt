package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 총 커밋 수 조회 UseCase
 *
 * - 데이터베이스에 저장된 모든 커밋의 총 개수를 가져옵니다.
 */
class GetTotalCommitCountUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(): Result<Int> = runSuspendCatching {
        commitRepository.getTotalCommitCount()
    }
}
