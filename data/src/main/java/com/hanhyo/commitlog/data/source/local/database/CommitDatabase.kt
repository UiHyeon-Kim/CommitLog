package com.hanhyo.commitlog.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hanhyo.commitlog.data.source.local.database.converter.LocalDateConverter
import com.hanhyo.commitlog.data.source.local.database.converter.MapTypeConverter
import com.hanhyo.commitlog.data.source.local.database.dao.CommitDao
import com.hanhyo.commitlog.data.source.local.database.dao.MonthlyReviewDao
import com.hanhyo.commitlog.data.source.local.database.entity.CommitEntity
import com.hanhyo.commitlog.data.source.local.database.entity.MonthlyReviewEntity

@Database(
    entities = [
        CommitEntity::class,
        MonthlyReviewEntity::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(
    LocalDateConverter::class,
    MapTypeConverter::class
)
abstract class CommitDatabase : RoomDatabase() {

    abstract fun commitDao(): CommitDao
    abstract fun monthlyReviewDao(): MonthlyReviewDao

    companion object {
        const val DATABASE_NAME = "commit_database"
    }
}
