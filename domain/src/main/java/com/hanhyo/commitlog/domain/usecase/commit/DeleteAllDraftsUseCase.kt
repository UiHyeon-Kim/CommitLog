package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 모든 임시저장 커밋 삭제 UseCase
 *
 * - 단일 Draft 상태 유지를 위해 임시저장된 모든 커밋 목록을 삭제합니다.
 */
class DeleteAllDraftsUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(): Result<Unit> = runSuspendCatching {
        commitRepository.deleteAllDrafts()
    }
}
