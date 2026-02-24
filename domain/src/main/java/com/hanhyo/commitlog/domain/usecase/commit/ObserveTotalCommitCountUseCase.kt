package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 총 커밋 수 실시간 관찰 UseCase
 *
 * - 전체 커밋 수의 변경사항을 실시간으로 관찰합니다. HomeViewModel이 Repository를 직접 호출하지 않도록 하기 위해 사용됩니다.
 */
class ObserveTotalCommitCountUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    operator fun invoke(): Flow<Int> {
        return commitRepository.observeTotalCommitCount()
    }
}
