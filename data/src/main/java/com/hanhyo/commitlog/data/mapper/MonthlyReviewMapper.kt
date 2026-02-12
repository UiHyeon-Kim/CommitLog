package com.hanhyo.commitlog.data.mapper

import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.MonthlyReview
import java.time.LocalDate


/** Commit 리스트와 AI 요약으로 MonthlyReview 생성 */
fun create(
    commits: List<Commit>,
    year: Int,
    month: Int,
    aiSummary: String
): MonthlyReview {
    // 주차별 커밋 수 계산
    val weeklyCommitCount = commits.groupBy { commit ->
        getWeekOfMonth(commit.date)
    }.mapValues { it.value.size }

    // Mood 분포 계산
    val moodDistribution = commits
        .mapNotNull { it.analysis?.mood }
        .groupingBy { it }
        .eachCount()

    // 태그 분포 계산
    val tagDistribution = commits
        .flatMap { it.tags }
        .groupingBy { it }
        .eachCount()

    return MonthlyReview(
        year = year,
        month = month,
        totalCommitCount = commits.size,
        weeklyCommitCount = weeklyCommitCount,
        moodDistribution = moodDistribution,
        tagDistribution = tagDistribution,
        aiSummary = aiSummary
    )
}

/** 날짜에서 월의 주차 계산 (1-5) */
private fun getWeekOfMonth(date: LocalDate): Int = ((date.dayOfMonth - 1) / 7) + 1
