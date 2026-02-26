package com.hanhyo.commitlog.presentation.ui.stats.model

import androidx.compose.runtime.Immutable

@Immutable
data class WeeklyStats(
    val focusTime: Int = 0,
    val commitCount: Int = 0,
    val productiveDays: List<ProductiveDay> = emptyList()
)

@Immutable
data class MonthlyStats(
    val commitFrequency: Int = 0,
    val streak: Int = 0,
    val moods: List<MoodDistribution> = emptyList(),
    val keywords: List<String> = emptyList()
)

@Immutable
data class YearlyStats(
    val totalRecords: Int = 0,
    val busiestMonth: String = "-",
    val skillGrowth: List<Skill> = emptyList()
)

@Immutable
data class ProductiveDay(
    val day: String,
    val count: Int,
    val description: String,
    val isTop: Boolean
)

@Immutable
data class MoodDistribution(
    val label: String,
    val percentage: Float,
    val color: androidx.compose.ui.graphics.Color
)

@Immutable
data class Skill(
    val name: String,
    val score: Float
)

enum class StatsPeriod(val title: String) {
    WEEKLY("주간"),
    MONTHLY("월간"),
    YEARLY("연간")
}
