package com.hanhyo.commitlog.domain.model

import java.time.LocalDate

// 타입 안정성, 객체 성능 비용 없음
@JvmInline
value class CommitId(val value: Long) {

    companion object {
        val NONE = CommitId(0L)
    }
}

@JvmInline
value class CommitTitle(val value: String) {

    init {
        require(value.isNotBlank()) { "제목은 비어있을 수 없습니다." }
    }
}

@JvmInline
value class LearnedContent(val value: String) {

    init {
        require(value.isNotBlank()) { "학습한 내용은 비어있을 수 없습니다." }
    }
}

@JvmInline
value class LearningTag(val value: String) {

    companion object {

        // 문자열을 LearningTag로 변환
        fun fromString(value: String): LearningTag? {
            return try {
                LearningTag(value.lowercase().trim())
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        // 문자열 리스트를 LearningTag Set으로 변환
        fun fromStringList(values: List<String>): Set<LearningTag> {
            return values.mapNotNull { fromString(it) }.toSet()
        }
    }
}

/**
 * 학습 기록
 * @param id 학습 기록 ID
 * @param date 학습 날짜
 * @param title 학습 제목
 * @param learnedToday 학습한 내용
 * @param difficulties 어려웠던 점
 * @param tomorrowPlan 내일 학습 계획
 * @param tags 학습 태그
 * @param analysis AI 학습 분석 결과
 * @param isDraft 학습이 Draft인지 여부
 * @param createdAt 학습 생성 시간
 * @param updatedAt 학습 수정 시간
 */
data class Commit(
    val id: CommitId,
    val date: LocalDate,
    val title: CommitTitle,
    val learnedToday: LearnedContent,
    val difficulties: String?,
    val tomorrowPlan: String?,
    val tags: Set<LearningTag>,
    val analysis: CommitAnalysis?,
    val isDraft: Boolean,
    val createdAt: Long,
    val updatedAt: Long?
) {
    /** Ai 분석 결과 추가 */
    fun withAnalysis(analysis: CommitAnalysis, tags: Set<LearningTag>): Commit {
        return copy(
            analysis = analysis,
            tags = tags,
            updatedAt = System.currentTimeMillis()
        )
    }

    /** Draft를 Commit으로 변환 */
    fun publish(): Commit {
        return copy(
            isDraft = false,
            updatedAt = System.currentTimeMillis()
        )
    }

    /** AI 분석 완료 여부 */
    fun isAnalyzed(): Boolean = analysis != null

    /** 오늘 작성한 Commit인지 확인 */
    fun isToday(): Boolean = date.isEqual(LocalDate.now())

    /** 어제 작성한 Commit인지 확인 */
    fun isYesterday(): Boolean = date.isEqual(LocalDate.now().minusDays(1))

    /** 검색어 매칭 여부 확인 */
    fun matchesSearchQuery(query: String): Boolean {
        if (query.isBlank()) return true

        val lowerQuery = query.lowercase()

        return title.value.lowercase().contains(lowerQuery) ||
                learnedToday.value.lowercase().contains(lowerQuery) ||
                difficulties?.lowercase()?.contains(lowerQuery) == true ||
                tomorrowPlan?.lowercase()?.contains(lowerQuery) == true ||
                tags.any { it.value.contains(lowerQuery) } ||
                analysis?.mood?.displayNameKo?.lowercase()?.contains(lowerQuery) == true ||
                analysis?.comment?.lowercase()?.contains(lowerQuery) == true
    }

    companion object {

        /** 새 Commit 생성 */
        fun create(
            date: LocalDate = LocalDate.now(),
            title: CommitTitle,
            learnedToday: LearnedContent,
            difficulties: String? = null,
            tomorrowPlan: String? = null,
            isDraft: Boolean = false
        ) = Commit(
            id = CommitId.NONE,
            date = date,
            title = title,
            learnedToday = learnedToday,
            difficulties = difficulties,
            tomorrowPlan = tomorrowPlan,
            tags = emptySet(),
            analysis = null,
            isDraft = isDraft,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )

        /** Draft 생성 */
        fun createDraft(
            date: LocalDate = LocalDate.now(),
            title: String = "",
            learnedToday: String = "",
        ) = Commit(
            id = CommitId.NONE,
            date = date,
            title = CommitTitle(title.ifBlank { "제목 없음" }),
            learnedToday = LearnedContent(learnedToday.ifBlank { "내용 없음" }),
            difficulties = null,
            tomorrowPlan = null,
            tags = emptySet(),
            analysis = null,
            isDraft = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )
    }
}

/**
 * AI 분석 결과
 * @param mood 학습의 감정
 * @param moodScore 학습의 감정 점수
 * @param difficultyLevel 학습의 어려움 수준
 * @param comment 학습의 분석 코멘트
 */
data class CommitAnalysis(
    val mood: AIMood,
    val moodScore: Int,
    val difficultyLevel: DifficultyLevel,
    val comment: String,
)

/**
 * 학습 감정
 * @param displayNameKo 한글 표시 이름
 * @param displayNameEn 영문 표시 이름
 * @param emoji 이모지
 * @param description 설명
 */
enum class AIMood(
    val displayNameKo: String,
    val displayNameEn: String,
    val emoji: String,
    val description: String,
) {
    CURIOUS(
        displayNameKo = "탐구적임",
        displayNameEn = "Curious",
        emoji = "💡",
        description = "새로운 개념을 이해하려는 상태"
    ),
    FOCUSED(
        displayNameKo = "집중함",
        displayNameEn = "Focused",
        emoji = "🎯",
        description = "방해 없이 몰입한 학습"
    ),
    PRODUCTIVE(
        displayNameKo = "성과적임",
        displayNameEn = "Productive",
        emoji = "✅",
        description = "결과물을 만들어낸 학습"
    ),
    CONFUSED(
        displayNameKo = "혼란스러움",
        displayNameEn = "Confused",
        emoji = "😕",
        description = "이해가 잘 되지 않는 상태"
    ),
    TIRED(
        displayNameKo = "지침",
        displayNameEn = "Tired",
        emoji = "😫",
        description = "에너지 소모가 큰 학습"
    ),
    RELIEVED(
        displayNameKo = "해결함",
        displayNameEn = "Relieved",
        emoji = "😌",
        description = "문제를 해결하고 안정된 상태"
    ),
    INSPIRED(
        displayNameKo = "영감받음",
        displayNameEn = "Inspired",
        emoji = "✨",
        description = "새로운 아이디어가 떠오른 상태"
    ),
    NORMAL(
        displayNameKo = "일상적임",
        displayNameEn = "Normal",
        emoji = "😐",
        description = "평범한 학습"
    );

    companion object {

        fun fromDisplayNameKo(name: String): AIMood? {
            return entries.find { it.displayNameKo == name }
        }

        fun fromDisplayNameEn(name: String): AIMood? {
            return entries.find { it.displayNameEn == name }
        }
    }
}

/**
 * 학습 어려움 수준
 * @param displayName 표시 이름
 * @param score 점수
 */
enum class DifficultyLevel(
    val displayName: String,
    val score: Int,
) {
    VERY_EASY("매우 쉬움", 1),
    EASY("쉬움", 2),
    NORMAL("보통", 3),
    HARD("어려움", 4),
    VERY_HARD("매우 어려움", 5);

    companion object {

        fun fromDisPlayName(name: String): DifficultyLevel? {
            return entries.find { it.displayName == name }
        }

        fun fromScore(score: Int): DifficultyLevel? {
            return entries.find { it.score == score }
        }
    }
}

/**
 * 월간 회고
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
        if (totalCommitCount == 0) return 0.0
        val count = moodDistribution[mood] ?: 0
        return count.toDouble() / totalCommitCount * 100
    }

    fun getTagPercentage(tag: LearningTag): Double {
        if (totalCommitCount == 0) return 0.0
        val count = tagDistribution[tag] ?: 0
        return count.toDouble() / totalCommitCount * 100
    }
}

/**
 * 연속 기록
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

/**
 * 검색 쿼리
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

/** AI 분석 결과 */
data class AiAnalysisResult(
    val analysis: CommitAnalysis,
    val tags: Set<LearningTag>
)
