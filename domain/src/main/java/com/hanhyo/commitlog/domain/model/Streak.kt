package com.hanhyo.commitlog.domain.model

import java.time.LocalDate

/**
 * 연속 기록
 *
 * @param currentStreak 현재 연속 기록
 * @param longestStreak 최장 연속 기록
 * @param lastCommitDate 마지막 Commit 날짜
 */
data class Streak(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastCommitDate: LocalDate?
) {
    /** 오늘이 연속된 날짜인지 여부 */
    fun isActive(): Boolean {
        if (lastCommitDate == null) return false

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return lastCommitDate.isEqual(today) || lastCommitDate.isEqual(yesterday)
    }

    companion object {
        fun empty() = Streak(0, 0, null)
    }
}
