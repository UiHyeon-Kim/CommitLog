package com.hanhyo.commitlog.data.di

import com.hanhyo.commitlog.data.repository.AiAnalysisRepositoryImpl
import com.hanhyo.commitlog.data.repository.CommitRepositoryImpl
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCommitRepository(
        repository: CommitRepositoryImpl
    ): CommitRepository

    @Binds
    @Singleton
    abstract fun bindAiAnalysisRepository(
        repository: AiAnalysisRepositoryImpl
    ): AiAnalysisRepository
}
