package com.hanhyo.commitlog.domain.model

/**
 * 학습 태그
 *
 * @param value 태그 값 (소문자, 20자 이하)
 */
@JvmInline
value class LearningTag(val value: String) {
    init {
        require(value.isNotBlank()) { "태그는 비어있을 수 없습니다" }
        require(value.length <= 20) { "태그는 20자를 초과할 수 없습니다" }
    }

    companion object {

        fun fromString(value: String): LearningTag? {
            return try {
                LearningTag(value.lowercase().trim())
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        fun fromStringList(values: List<String>): Set<LearningTag> {
            return values.mapNotNull { fromString(it) }.toSet()
        }
    }
}
