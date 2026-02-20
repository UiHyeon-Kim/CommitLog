package com.hanhyo.commitlog.domain.model

/**
 * 월간 회고
 *
 * @param year 연도
 * @param month 월
 * @param totalCommitCount 총 Commit 개수
 * @param weeklyCommitCount 주간 Commit 개수
 * @param moodDistribution 감정 분포
 * @param tagDistribution 태그 분포
 * @param aiSummary AI 요약
 * @param generatedAt 생성 시간
 */
data class MonthlyReview(
    val year: Int,
    val month: Int,
    val totalCommitCount: Int,
    val weeklyCommitCount: Map<Int, Int>,
    val moodDistribution: Map<AIMood, Int>,
    val tagDistribution: Map<LearningTag, Int>,
    val aiSummary: String,
    val generatedAt: Long = System.currentTimeMillis()
) {
    fun getMostFrequentMood(): AIMood? {
        return moodDistribution.maxByOrNull { it.value }?.key
    }

    fun getMostFrequentTag(): LearningTag? {
        return tagDistribution.maxByOrNull { it.value }?.key
    }

    fun getMoodPercentage(mood: AIMood): Double {
        val total = moodDistribution.values.sum()
        if (total == 0) return 0.0
        val count = moodDistribution[mood] ?: 0
        return count.toDouble() / total * 100
    }

    fun getTagPercentage(tag: LearningTag): Double {
        val total = tagDistribution.values.sum()
        if (total == 0) return 0.0
        val count = tagDistribution[tag] ?: 0
        return count.toDouble() / total * 100
    }
}
