package com.hanhyo.commitlog.data.di

import com.hanhyo.commitlog.data.repository.AiAnalysisRepositoryImpl
import com.hanhyo.commitlog.data.repository.CommitRepositoryImpl
import com.hanhyo.commitlog.data.source.local.database.dao.CommitDao
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideCommitRepository(
        commitDao: CommitDao
    ): CommitRepository {
        return CommitRepositoryImpl(commitDao)
    }
    
    @Provides
    @Singleton
    fun provideAiAnalysisRepository(): AiAnalysisRepository {
        return AiAnalysisRepositoryImpl()
    }
}
