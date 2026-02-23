package com.hanhyo.commitlog.data.mapper

import com.hanhyo.commitlog.data.source.local.database.entity.MonthlyReviewEntity
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.MonthlyReview
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

object MonthlyReviewMapper {

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
    private fun getWeekOfMonth(date: LocalDate): Int {
        val weekFields = WeekFields.of(Locale.KOREA)
        return date.get(weekFields.weekOfMonth())
    }

    /** Domain -> Entity 매핑 */
    fun mapToEntity(domain: MonthlyReview): MonthlyReviewEntity {
        return MonthlyReviewEntity(
            year = domain.year,
            month = domain.month,
            totalCommitCount = domain.totalCommitCount,
            weeklyCommitCount = domain.weeklyCommitCount,
            moodDistribution = domain.moodDistribution.mapKeys { it.key.name },
            tagDistribution = domain.tagDistribution.mapKeys { it.key.value },
            aiSummary = domain.aiSummary,
            generatedAt = domain.generatedAt
        )
    }

    /** Entity -> Domain 매핑 */
    fun mapToDomain(entity: MonthlyReviewEntity): MonthlyReview {
        return MonthlyReview(
            year = entity.year,
            month = entity.month,
            totalCommitCount = entity.totalCommitCount,
            weeklyCommitCount = entity.weeklyCommitCount,
            moodDistribution = entity.moodDistribution.mapKeys { com.hanhyo.commitlog.domain.model.AIMood.fromName(it.key) },
            tagDistribution = entity.tagDistribution.mapNotNull {
                val tag = com.hanhyo.commitlog.domain.model.LearningTag.fromString(it.key)
                if (tag != null) tag to it.value else null
            }.toMap(),
            aiSummary = entity.aiSummary,
            generatedAt = entity.generatedAt
        )
    }
}
