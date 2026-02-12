package com.hanhyo.commitlog.data.repository

import com.hanhyo.commitlog.data.mapper.toDomain
import com.hanhyo.commitlog.data.mapper.toDomainList
import com.hanhyo.commitlog.data.mapper.toEntity
import com.hanhyo.commitlog.data.source.local.database.dao.CommitDao
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.SearchQuery
import com.hanhyo.commitlog.domain.model.Streak
import com.hanhyo.commitlog.domain.repository.CommitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommitRepositoryImpl @Inject constructor(
    private val commitDao: CommitDao
) : CommitRepository {

    override fun observeAllCommits(): Flow<List<Commit>> =
        commitDao.observeAllCommits().map { it.toDomainList() }

    override fun observeAllDrafts(): Flow<List<Commit>> =
        commitDao.observeAllDrafts().map { it.toDomainList() }

    override suspend fun getCommitById(id: CommitId): Commit? =
        commitDao.getCommitById(id.value)?.toDomain()

    override suspend fun getCommitsByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Commit> {
        val entities = commitDao.getCommitsByDateRange(
            startDate = startDate,
            endDate = endDate
        )
        return entities.toDomainList()
    }

    override suspend fun getCommitsForMonth(year: Int, month: Int): List<Commit> {
        val yearMonth = YearMonth.of(year, month)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return commitDao.getCommitsByDateRange(startDate, endDate).map { it.toDomain() }
    }

    override suspend fun searchCommits(query: SearchQuery): List<Commit> {
        val entities = if (query.keyword.isNotBlank()) {
            commitDao.searchByKeyword(query.keyword)
        } else {
            emptyList()
        }

        val commits = entities.toDomainList()

        // 추가 필터링 (태그, Mood, 날짜 범위)
        return commits.filter { commit -> query.matches(commit) }
    }

    override suspend fun getTotalCommitCount(): Int {
        return commitDao.getTotalCommitCount()
    }

    override suspend fun saveCommit(commit: Commit): Long {
        val entity = commit.toEntity()
        return commitDao.insertCommit(entity)
    }

    override suspend fun updateCommit(commit: Commit) {
        val entity = commit.toEntity()
        commitDao.updateCommit(entity)
    }

    override suspend fun deleteCommit(commit: Commit) {
        val entity = commit.toEntity()
        commitDao.deleteCommit(entity)
    }

    override suspend fun calculateStreak(): Streak {
        val recentEntities = commitDao.getRecentCommits(100)
        val recentCommits = recentEntities.toDomainList()

        if (recentCommits.isEmpty()) return Streak.empty()

        // 날짜 추출 및 정렬 (최신순)
        val dates = recentCommits
            .map { it.date }
            .distinct()
            .sortedDescending()

        // 현재 Streak 계산
        val today = LocalDate.now()
        var currentStreak = 0
        var checkDate = today

        for (date in dates) {
            val daysBetween = ChronoUnit.DAYS.between(date, checkDate)

            // 연속된 날짜인 경우 (오늘 또는 하루 차이)
            if (daysBetween == 0L || daysBetween == 1L) {
                currentStreak++
                checkDate = date.minusDays(1)
            } else {
                break
            }
        }

        // 최장 Streak 계산
        var longestStreak = 1
        var tempStreak = 1

        for (i in 1 until dates.size) {
            val daysBetween = ChronoUnit.DAYS.between(dates[i], dates[i - 1])

            if (daysBetween == 1L) {
                tempStreak++
                longestStreak = maxOf(longestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }

        return Streak(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastCommitDate = dates.firstOrNull()
        )
    }
}
