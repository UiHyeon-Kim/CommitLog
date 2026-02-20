package com.hanhyo.commitlog.domain.model

/**
 * 학습 어려움 수준
 *
 * @param displayName 표시 이름
 * @param score 점수
 * @param emoji 이모지
 */
enum class DifficultyLevel(
    val displayName: String,
    val score: Int,
    val emoji: String,
) {
    VERY_EASY("매우 쉬움", 1, "😆"),
    EASY("쉬움", 2, "🙂"),
    NORMAL("보통", 3, "😐"),
    HARD("어려움", 4, "😫"),
    VERY_HARD("매우 어려움", 5, "🤯");

    companion object {

        fun fromDisplayName(name: String): DifficultyLevel? {
            return entries.find { it.displayName == name }
        }

        fun fromScore(score: Int): DifficultyLevel? {
            return entries.find { it.score == score }
        }
    }
}
