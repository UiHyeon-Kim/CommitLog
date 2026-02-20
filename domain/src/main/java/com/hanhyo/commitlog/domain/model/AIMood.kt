package com.hanhyo.commitlog.domain.model

/**
 * 학습 감정
 *
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
        emoji = "😊",
        description = "평범한 학습"
    );

    companion object {

        /** DB 저장용 */
        fun fromName(name: String?): AIMood {
            return entries.find { it.name == name } ?: NORMAL
        }

        /** UI 표시용 */
        fun fromDisplayNameKo(displayName: String?): AIMood {
            return entries.find { it.displayNameKo == displayName } ?: NORMAL
        }
    }
}
