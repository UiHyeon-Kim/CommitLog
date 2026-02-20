package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * ID로 특정 커밋 조회 UseCase
 *
 * - 고유 ID를 사용하여 특정 커밋 정보를 데이터베이스에서 가져옵니다. ID에 해당하는 커밋이 없으면 예외를 발생시킵니다.
 */
class GetCommitByIdUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(id: CommitId): Result<Commit> = runSuspendCatching {
        commitRepository.getCommitById(id)
            ?: throw NoSuchElementException("커밋을 찾을 수 없습니다")
    }
}
