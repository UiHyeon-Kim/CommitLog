package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.SearchQuery
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

/**
 * 커밋 검색 UseCase
 *
 * - 주어진 검색 쿼리(제목, 내용, 태그)와 일치하는 커밋 목록을 반환합니다.
 */
class SearchCommitsUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(query: SearchQuery): Result<List<Commit>> = runSuspendCatching {
        commitRepository.searchCommits(query)
    }
}
