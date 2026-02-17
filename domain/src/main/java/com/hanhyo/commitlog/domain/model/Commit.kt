package com.hanhyo.commitlog.domain.model

import java.time.LocalDate

/** 커밋 ID (타입 안정성, 객체 성능 비용 없음) */
@JvmInline
value class CommitId(val value: Long) {

    fun isValid(): Boolean = value > 0

    companion object {
        val NONE = CommitId(0L)
    }
}

/** 커밋 제목 */
@JvmInline
value class CommitTitle(val value: String) {

    init {
        require(value.isNotBlank()) { "제목은 비어있을 수 없습니다." }
    }
}

/** 학습 내용 */
@JvmInline
value class LearnedContent(val value: String) {

    init {
        require(value.isNotBlank()) { "학습한 내용은 비어있을 수 없습니다." }
    }
}

/**
 * 학습 기록
 *
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
    /** AI 분석 결과 추가 */
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
