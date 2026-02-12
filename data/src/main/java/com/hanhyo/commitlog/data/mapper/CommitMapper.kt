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

object CommitMapper {

    fun CommitEntity.toDomain(): Commit {
        return Commit(
            id = CommitId(id),
            date = date,
            title = CommitTitle(title),
            learnedToday = LearnedContent(learnedToday),
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
            aiMood = analysis?.mood?.displayNameKo,
            moodScore = analysis?.moodScore,
            difficultyLevel = analysis?.difficultyLevel?.displayName,
            aiComment = analysis?.comment,
            isDraft = isDraft,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun toDomainList(entities: List<CommitEntity>): List<Commit> =
        entities.map { it.toDomain() }

    /** Entity AI 분석 결과 -> CommitAnalysis */
    private fun CommitEntity.toAnalysis(): CommitAnalysis? {
        if (aiMood == null || moodScore == null || difficultyLevel == null || aiComment == null) return null

        val mood = AIMood.fromDisplayNameKo(aiMood) ?: return null

        val difficulty = DifficultyLevel.fromDisplayName(difficultyLevel) ?: return null

        return try {
            CommitAnalysis(
                mood = mood,
                moodScore = moodScore,
                difficultyLevel = difficulty,
                comment = aiComment
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun serializeTags(tags: Set<LearningTag>): String =
        tags.joinToString(",") { it.value }

    private fun parseTags(tagsString: String): Set<LearningTag> {
        if (tagsString.isBlank()) return emptySet()

        return tagsString.split(",")
            .mapNotNull { tagValue ->
                LearningTag.fromString(tagValue.trim())
            }
            .toSet()
    }
}
