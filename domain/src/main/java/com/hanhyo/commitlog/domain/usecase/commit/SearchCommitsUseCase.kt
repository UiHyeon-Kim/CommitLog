package com.hanhyo.commitlog.domain.usecase.commit

import com.hanhyo.commitlog.domain.common.DomainError
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.SearchQuery
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class SearchCommitsUseCase @Inject constructor(
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(query: SearchQuery): Result<List<Commit>> {
        return try {
            val count = commitRepository.searchCommits(query)
            Result.success(count)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.error(DomainError.DatabaseError("검색 실패", e))
        }
    }
}
