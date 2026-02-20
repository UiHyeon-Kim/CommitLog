package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 특정 커밋 실시간 관찰 UseCase
 *
 * - 고유 ID를 사용하여 특정 커밋의 변경사항을 실시간으로 관찰(observe)합니다. 커밋이 변경될 때마다 새로운 데이터를 전달받습니다.
 */
class ObserveCommitUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    operator fun invoke(commitId: CommitId): Flow<Commit?> {
        return commitRepository.observeCommitById(commitId)
    }
}
