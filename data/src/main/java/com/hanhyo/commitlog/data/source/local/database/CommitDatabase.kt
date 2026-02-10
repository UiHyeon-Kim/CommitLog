package com.hanhyo.commitlog.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hanhyo.commitlog.data.source.local.database.dao.CommitDao
import com.hanhyo.commitlog.data.source.local.database.entity.CommitEntity

@Database(
    entities = [CommitEntity::class],
    version = 1,
    exportSchema = true
)
abstract class CommitDatabase : RoomDatabase() {

    abstract fun commitDao(): CommitDao

    companion object {
        const val DATABASE_NAME = "commit_database"
    }
}
