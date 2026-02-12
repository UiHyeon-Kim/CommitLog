package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllCommitsUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    operator fun invoke(): Flow<List<Commit>> {
        return commitRepository.observeAllCommits()
    }
}
