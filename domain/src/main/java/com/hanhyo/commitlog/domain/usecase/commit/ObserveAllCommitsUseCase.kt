package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 모든 커밋 실시간 관찰 UseCase
 *
 * - 임시 저장된 커밋을 제외한 모든 커밋 목록의 변경사항을 실시간으로 관찰합니다.
 */
class ObserveAllCommitsUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    operator fun invoke(): Flow<List<Commit>> {
        return commitRepository.observeAllCommits()
    }
}
