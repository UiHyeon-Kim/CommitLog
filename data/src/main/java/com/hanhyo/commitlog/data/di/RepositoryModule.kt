package com.hanhyo.commitlog.data.di

import com.hanhyo.commitlog.data.repository.AiAnalysisRepositoryImpl
import com.hanhyo.commitlog.data.repository.CommitRepositoryImpl
import com.hanhyo.commitlog.data.scheduler.ReviewSchedulerImpl
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import com.hanhyo.commitlog.domain.scheduler.ReviewScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCommitRepository(
        repository: CommitRepositoryImpl
    ): CommitRepository

    @Binds
    abstract fun bindAiAnalysisRepository(
        repository: AiAnalysisRepositoryImpl
    ): AiAnalysisRepository

    @Binds
    abstract fun bindReviewScheduler(
        scheduler: ReviewSchedulerImpl
    ): ReviewScheduler
}
