package com.hanhyo.commitlog.data.mapper

import com.hanhyo.commitlog.data.source.local.database.entity.CommitEntity
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitAnalysis
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.DifficultyLevel
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.LearningTag
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun CommitEntity.toDomain(): Commit {
    return Commit(
        id = CommitId(id),
        date = date,
        title = CommitTitle(title.ifBlank { "제목 없음" }),
        learnedToday = LearnedContent(learnedToday.ifBlank { "내용 없음" }),
        difficulties = difficulties,
        tomorrowPlan = tomorrowPlan,
        tags = parseTags(tags),
        analysis = this.toAnalysis(),
        isDraft = isDraft,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Commit.toEntity(): CommitEntity {
    return CommitEntity(
        id = id.value,
        date = date,
        title = title.value,
        learnedToday = learnedToday.value,
        difficulties = difficulties,
        tomorrowPlan = tomorrowPlan,
        tags = serializeTags(tags),
        aiMood = analysis?.mood?.name,
        moodScore = analysis?.moodScore,
        difficultyLevel = analysis?.difficultyLevel?.name,
        aiComment = analysis?.comment,
        isDraft = isDraft,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<CommitEntity>.toDomainList(): List<Commit> =
    this.map { it.toDomain() }

/** Entity AI 분석 결과 -> CommitAnalysis */
private fun CommitEntity.toAnalysis(): CommitAnalysis? {
    if (aiMood == null || moodScore == null || difficultyLevel == null || aiComment == null) return null

    val mood = AIMood.entries.find { it.name == aiMood } ?: AIMood.NORMAL
    val difficulty = DifficultyLevel.entries.find { it.name == difficultyLevel } ?: DifficultyLevel.NORMAL

    return CommitAnalysis(
        mood = mood,
        moodScore = moodScore,
        difficultyLevel = difficulty,
        comment = aiComment
    )
}

private fun serializeTags(tags: Set<LearningTag>): String =
    json.encodeToString(tags.map { it.value })

private fun parseTags(tagsString: String): Set<LearningTag> {
    if (tagsString.isBlank()) return emptySet()

    return try {
        json.decodeFromString<List<String>>(tagsString)
            .mapNotNull { tagValue ->
                LearningTag.fromString(tagValue.trim())
            }
            .toSet()
    } catch (_: SerializationException) {
        emptySet()
    }
}
