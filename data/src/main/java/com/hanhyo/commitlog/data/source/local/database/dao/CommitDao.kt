package com.hanhyo.commitlog.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hanhyo.commitlog.data.source.local.database.entity.CommitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommitDao {

    /** 모든 Commit 최신순 관찰 */
    @Query("SELECT * FROM commits ORDER BY date DESC")
    fun observeAllCommits(): Flow<List<CommitEntity>>

    /** Draft 제외 모든 Commit 최신순 관찰 */
    @Query("SELECT * FROM commits WHERE isDraft = 0 ORDER BY date DESC")
    fun observeAllPublishedCommits(): Flow<List<CommitEntity>>

    /** Draft만 조회 */
    @Query("SELECT * FROM commits WHERE isDraft = 1 ORDER BY updatedAt DESC")
    fun observeAllDrafts(): Flow<List<CommitEntity>>

    /** Commit 삽입 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommit(commit: CommitEntity): Long

    /** Commit 업데이트 */
    @Update
    suspend fun updateCommit(commit: CommitEntity)

    /** Commit 삭제 */
    @Delete
    suspend fun deleteCommit(commit: CommitEntity)

    /** 총 Commit 개수 */
    @Query("SELECT COUNT(*) FROM commits WHERE isDraft = 0")
    suspend fun getTotalCommitCount(): Int

    /** Streak 계산용 최근 Commit */
    @Query("SELECT * FROM commits WHERE isDraft = 0 ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentCommits(limit: Int = 100): List<CommitEntity>


    // 테스트용
    /** 모든 Commit 삭제 */
    @Query("DELETE FROM commits")
    suspend fun deleteAllCommits()

    /** 모든 Draft 삭제 */
    @Query("DELETE FROM commits WHERE isDraft = 1")
    suspend fun deleteAllDrafts()

}
