package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.SearchQuery
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

class SearchCommitsUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(query: SearchQuery): Result<List<Commit>> = runSuspendCatching {
        commitRepository.searchCommits(query)
    }
}
