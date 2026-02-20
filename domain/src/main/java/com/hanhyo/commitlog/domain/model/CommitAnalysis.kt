package com.hanhyo.commitlog.domain.model

/**
 * AI 분석 결과
 *
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

/** AI 분석 결과 (분석 + 태그) */
data class AiAnalysisResult(
    val analysis: CommitAnalysis,
    val tags: Set<LearningTag>
)
