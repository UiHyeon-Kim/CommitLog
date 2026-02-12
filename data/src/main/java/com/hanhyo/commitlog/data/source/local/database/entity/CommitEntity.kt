package com.hanhyo.commitlog.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "commits",
    indices = [
        Index(value = ["date"]),
        Index(value = ["createdAt"])
    ]
)
data class CommitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: LocalDate,   // yyyy-MM-dd 형식의 날짜
    val title: String,
    val learnedToday: String,   // 오늘 배운점
    val difficulties: String?,  // 어려웠던 점
    val tomorrowPlan: String?,  // 내일 계획
    val tags: String,

    val aiMood: String?,    // 이 커밋에서 느껴지는 감정
    val moodScore: Int?,    // 긍정 부정에 따른 감정 점수
    val difficultyLevel: String?,
    val aiComment: String?,

    val isDraft: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long?
)
