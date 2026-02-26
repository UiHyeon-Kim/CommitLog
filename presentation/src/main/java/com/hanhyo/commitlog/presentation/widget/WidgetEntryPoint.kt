package com.hanhyo.commitlog.presentation.widget

import com.hanhyo.commitlog.domain.usecase.commit.GetStreakUseCase
import com.hanhyo.commitlog.domain.usecase.commit.GetTodayCommitUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Glance 위젯에서 Hilt 의존성 주입을 사용하기 위한 EntryPoint 인터페이스
 * Glance AppWidget은 직접적인 Hilt 주입을 지원하지 않으므로 이 방식을 사용
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    /** 스트릭 정보를 가져오는 UseCase */
    fun getStreakUseCase(): GetStreakUseCase
    /** 오늘 커밋 정보를 가져오는 UseCase */
    fun getTodayCommitUseCase(): GetTodayCommitUseCase
}
