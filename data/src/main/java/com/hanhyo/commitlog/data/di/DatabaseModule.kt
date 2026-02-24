package com.hanhyo.commitlog.data.di

import android.content.Context
import androidx.room.Room
import com.hanhyo.commitlog.data.BuildConfig
import com.hanhyo.commitlog.data.source.local.database.CommitDatabase
import com.hanhyo.commitlog.data.source.local.database.dao.CommitDao
import com.hanhyo.commitlog.data.source.local.database.dao.MonthlyReviewDao
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
    fun provideCommitDatabase(
        @ApplicationContext context: Context
    ): CommitDatabase {
        return Room.databaseBuilder(
            context,
            CommitDatabase::class.java,
            CommitDatabase.DATABASE_NAME
        )
            .apply {
                fallbackToDestructiveMigration(true)
            }
            .build()
    }

    @Provides
    fun provideCommitDao(
        database: CommitDatabase
    ): CommitDao {
        return database.commitDao()
    }

    @Provides
    fun provideMonthlyReviewDao(
        database: CommitDatabase
    ): MonthlyReviewDao {
        return database.monthlyReviewDao()
    }
}
