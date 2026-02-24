package com.hanhyo.commitlog.presentation.ui.stats.model

data class WeeklyStats(
    val focusTime: Int = 0,
    val commitCount: Int = 0,
    val productiveDays: List<ProductiveDay> = emptyList()
)

data class MonthlyStats(
    val commitFrequency: Int = 0,
    val streak: Int = 0,
    val moods: List<MoodDistribution> = emptyList(),
    val keywords: List<String> = emptyList()
)

data class YearlyStats(
    val totalRecords: Int = 0,
    val busiestMonth: String = "-",
    val skillGrowth: List<Skill> = emptyList()
)

data class ProductiveDay(
    val day: String,
    val count: Int,
    val description: String,
    val isTop: Boolean
)

data class MoodDistribution(
    val label: String,
    val percentage: Float,
    val color: androidx.compose.ui.graphics.Color
)

data class Skill(
    val name: String,
    val score: Float
)

enum class StatsPeriod(val title: String) {
    WEEKLY("주간"),
    MONTHLY("월간"),
    YEARLY("연간")
}
