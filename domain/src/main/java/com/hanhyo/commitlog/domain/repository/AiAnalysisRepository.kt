package com.hanhyo.commitlog.domain.repository

import com.hanhyo.commitlog.domain.model.AiAnalysisResult
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.MonthlyReview
import kotlinx.coroutines.flow.Flow

interface AiAnalysisRepository {

    /**
     * 커밋 내용 분석
     *
     * 분석 내용: Mood, MoodScore, DifficultyLevel, Comment, Tags
     *
     * @param title 제목
     * @param learnedToday 오늘 배운 내용
     * @param difficulties 어려웠던 점
     * @param tomorrowPlan 내일 할 일
     * @return 분석 결과 (CommitAnalysis + LearningTag Set)
     */
    suspend fun analyzeCommit(
        title: CommitTitle,
        learnedToday: LearnedContent,
        difficulties: String?,
        tomorrowPlan: String?,
    ): AiAnalysisResult

    suspend fun scheduleAnalysis(commitId: Long)

    suspend fun generateMonthlyReview(
        commits: List<Commit>,
        year: Int,
        month: Int,
    ): MonthlyReview

    /**
     * 월간 회고 생성을 백그라운드 작업으로 예약합니다.
     */
    suspend fun scheduleMonthlyReview(year: Int, month: Int)

    /**
     * 특정 월의 회고 데이터를 관찰합니다.
     */
    fun observeMonthlyReview(year: Int, month: Int): Flow<MonthlyReview?>

    /**
     * 특정 월의 회고 데이터를 삭제합니다.
     */
    suspend fun deleteMonthlyReview(year: Int, month: Int)
}
