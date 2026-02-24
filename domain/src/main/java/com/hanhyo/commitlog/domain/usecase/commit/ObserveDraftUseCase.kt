package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 단일 임시저장 커밋 실시간 관찰 UseCase
 *
 * - 저장되지 않고 임시 저장 상태인 단 하나의 커밋(초안) 변경사항을 실시간으로 관찰합니다.
 */
class ObserveDraftUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    operator fun invoke(): Flow<Commit?> {
        return commitRepository.observeDraft()
    }
}
