package com.hanhyo.commitlog.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hanhyo.commitlog.data.source.local.database.entity.MonthlyReviewEntity

@Dao
interface MonthlyReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyReview(review: MonthlyReviewEntity)

    @Query("SELECT * FROM monthly_reviews WHERE year = :year AND month = :month")
    suspend fun getMonthlyReview(year: Int, month: Int): MonthlyReviewEntity?

    @Query("SELECT * FROM monthly_reviews WHERE year = :year AND month = :month")
    fun observeMonthlyReview(year: Int, month: Int): kotlinx.coroutines.flow.Flow<MonthlyReviewEntity?>
}
