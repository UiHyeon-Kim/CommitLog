package com.hanhyo.commitlog.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hanhyo.commitlog.data.source.local.database.entity.CommitEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CommitDao {

    /** 모든 Commit 최신순 관찰 */
    @Query("SELECT * FROM commits WHERE isDraft = 0 ORDER BY date DESC, createdAt DESC")
    fun observeAllCommits(): Flow<List<CommitEntity>>

    /** Draft만 조회 */
    @Query("SELECT * FROM commits WHERE isDraft = 1 ORDER BY (updatedAt IS NULL) DESC, updatedAt DESC, createdAt DESC")
    fun observeAllDrafts(): Flow<List<CommitEntity>>

    /** 특정 Commit 조회 */
    @Query("SELECT * FROM commits WHERE id = :id")
    suspend fun getCommitById(id: Long): CommitEntity?

    /** 날짜 범위로 커밋 조회 */
    @Query("SELECT * FROM commits WHERE date BETWEEN :startDate AND :endDate AND isDraft = 0 ORDER BY date DESC, createdAt DESC")
    suspend fun getCommitsByDateRange(startDate: LocalDate, endDate: LocalDate): List<CommitEntity>

    /**
     * 키워드로 커밋 검색
     * 제목, 학습 내용, 어려운 점, 내일 할 일, AI 코멘트, 태그에서 검색
     */
    @Query("""
        SELECT * FROM commits 
        WHERE isDraft = 0 
        AND (
            title LIKE '%' || :keyword || '%' 
            OR learnedToday LIKE '%' || :keyword || '%' 
            OR difficulties LIKE '%' || :keyword || '%' 
            OR tomorrowPlan LIKE '%' || :keyword || '%'
            OR aiComment LIKE '%' || :keyword || '%'
            OR tags LIKE '%' || :keyword || '%'
        )
        ORDER BY date DESC, createdAt DESC
    """)
    suspend fun searchByKeyword(keyword: String): List<CommitEntity>

    /** 총 Commit 개수 */
    @Query("SELECT COUNT(*) FROM commits WHERE isDraft = 0")
    fun observeTotalCommitCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM commits WHERE isDraft = 0")
    suspend fun getTotalCommitCount(): Int

    /** Streak 계산용 최근 Commit */
    @Query("SELECT * FROM commits WHERE isDraft = 0 ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentCommits(limit: Int = 100): List<CommitEntity>

    /** Commit 삽입 */
    @Insert
    suspend fun insertCommit(commit: CommitEntity): Long

    /** Commit 업데이트 */
    @Update
    suspend fun updateCommit(commit: CommitEntity)

    /** Commit 삭제 */
    @Delete
    suspend fun deleteCommit(commit: CommitEntity)


    // 테스트용
    /** 모든 Commit 삭제 */
    @Query("DELETE FROM commits")
    suspend fun deleteAllCommits()

    /** 모든 Draft 삭제 */
    @Query("DELETE FROM commits WHERE isDraft = 1")
    suspend fun deleteAllDrafts()

}
