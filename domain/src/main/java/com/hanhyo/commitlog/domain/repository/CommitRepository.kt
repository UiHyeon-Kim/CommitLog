package com.hanhyo.commitlog.domain.repository

import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.SearchQuery
import com.hanhyo.commitlog.domain.model.Streak
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CommitRepository {

    /** 모든 커밋 관찰 - 홈 실시간 커밋 목록 */
    fun observeAllCommits(): Flow<List<Commit>>

    /** 모든 초안 관찰 - 작성 중 초안 목록 */
    fun observeAllDrafts(): Flow<List<Commit>>

    /** ID로 커밋 조회 - 상세, 수정 화면 */
    suspend fun getCommitById(id: CommitId): Commit?

    /** 날짜 범위 조회 - 통계 화면 */
    suspend fun getCommitsByDateRange(startDate: LocalDate, endDate: LocalDate): List<Commit>

    /** 특정 월 커밋 조회 - 월간 회고 */
    suspend fun getCommitsForMonth(year: Int, month: Int): List<Commit>

    /** 검색 */
    suspend fun searchCommits(query: SearchQuery): List<Commit>

    /** 총 커밋 수 - 통계 화면 */
    fun observeTotalCommitCount(): Flow<Int>

    suspend fun getTotalCommitCount(): Int

    /** Streak 계산 */
    suspend fun calculateStreak(): Streak

    suspend fun saveCommit(commit: Commit): Long

    suspend fun updateCommit(commit: Commit)

    suspend fun deleteCommit(commit: Commit)

}
