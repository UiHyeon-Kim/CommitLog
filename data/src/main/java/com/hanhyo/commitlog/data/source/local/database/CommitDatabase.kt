package com.hanhyo.commitlog.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hanhyo.commitlog.data.source.local.database.converter.LocalDateConverter
import com.hanhyo.commitlog.data.source.local.database.dao.CommitDao
import com.hanhyo.commitlog.data.source.local.database.entity.CommitEntity

@Database(
    entities = [CommitEntity::class],
    version = 2,
    autoMigrations = [
        androidx.room.AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
@TypeConverters(LocalDateConverter::class)
abstract class CommitDatabase : RoomDatabase() {

    abstract fun commitDao(): CommitDao

    companion object {
        const val DATABASE_NAME = "commit_database"
    }
}
