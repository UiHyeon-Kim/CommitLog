package com.hanhyo.commitlog.domain.repository

import com.hanhyo.commitlog.domain.model.AiAnalysisResult
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.MonthlyReview

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

    suspend fun generateMonthlyReview(
        commits: List<Commit>,
        year: Int,
        month: Int,
    ): MonthlyReview

}
