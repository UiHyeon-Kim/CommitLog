package com.hanhyo.commitlog.domain.model

import java.time.LocalDate

/**
 * 검색 쿼리
 *
 * @param keyword 검색어
 * @param tags 태그
 * @param moods 감정
 * @param startDate 시작 날짜
 * @param endDate 종료 날짜
 * @param includeDrafts 포함한 Draft인지 여부
 */
data class SearchQuery(
    val keyword: String = "",
    val tags: Set<LearningTag> = emptySet(),
    val moods: Set<AIMood> = emptySet(),
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val includeDrafts: Boolean = false
) {
    fun isEmpty(): Boolean {
        return keyword.isBlank() &&
                tags.isEmpty() &&
                moods.isEmpty() &&
                startDate == null &&
                endDate == null
    }

    fun matches(commit: Commit): Boolean {
        if (!includeDrafts && commit.isDraft) return false

        if (keyword.isNotBlank() && !commit.matchesSearchQuery(keyword)) return false

        if (tags.isNotEmpty() && tags.none { it in commit.tags }) return false

        if (moods.isNotEmpty()) {
            val commitMood = commit.analysis?.mood
            if (commitMood == null || commitMood !in moods) {
                return false
            }
        }

        if (startDate != null && commit.date < startDate) return false

        if (endDate != null && commit.date > endDate) return false

        return true
    }
}
