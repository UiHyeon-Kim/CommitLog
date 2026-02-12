package com.hanhyo.commitlog.data.di

import android.content.Context
import androidx.room.Room
import com.hanhyo.commitlog.data.source.local.database.CommitDatabase
import com.hanhyo.commitlog.data.source.local.database.dao.CommitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CommitDatabase {
        return Room.databaseBuilder(
            context,
            CommitDatabase::class.java,
            CommitDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true) // TODO: 개발 완료 후 삭제
            .build()
    }

    @Provides
    fun provideCommitDao(
        database: CommitDatabase
    ): CommitDao {
        return database.commitDao()
    }
}
