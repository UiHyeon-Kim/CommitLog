package com.hanhyo.commitlog.data.mapper

import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.MonthlyReview
import java.time.LocalDate

object MonthlyReviewMapper {

    /** Commit 리스트와 AI 요약으로 MonthlyReview 생성 */
    fun create(
        commits: List<Commit>,
        year: Int,
        month: Int,
        aiSummary: String
    ): MonthlyReview {

        val filtered = commits.filter {
            it.date.year == year && it.date.monthValue == month
        }

        // 주차별 커밋 수 계산
        val weeklyCommitCount = filtered.groupBy { commit ->
            getWeekOfMonth(commit.date)
        }.mapValues { it.value.size }

        // Mood 분포 계산
        val moodDistribution = filtered
            .mapNotNull { it.analysis?.mood }
            .groupingBy { it }
            .eachCount()

        // 태그 분포 계산
        val tagDistribution = filtered
            .flatMap { it.tags }
            .groupingBy { it }
            .eachCount()

        return MonthlyReview(
            year = year,
            month = month,
            totalCommitCount = filtered.size,
            weeklyCommitCount = weeklyCommitCount,
            moodDistribution = moodDistribution,
            tagDistribution = tagDistribution,
            aiSummary = aiSummary
        )
    }

    /** 날짜에서 월의 주차 계산 (1-5) */
    private fun getWeekOfMonth(date: LocalDate): Int = ((date.dayOfMonth - 1) / 7) + 1
}
