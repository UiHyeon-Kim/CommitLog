package com.hanhyo.commitlog.data.source.local.database.entity

import androidx.room.Entity

@Entity(
    tableName = "monthly_reviews",
    primaryKeys = ["year", "month"]
)
data class MonthlyReviewEntity(
    val year: Int,
    val month: Int,
    val totalCommitCount: Int,
    val weeklyCommitCount: Map<Int, Int>,
    val moodDistribution: Map<String, Int>,
    val tagDistribution: Map<String, Int>,
    val aiSummary: String,
    val generatedAt: Long = System.currentTimeMillis()
)
