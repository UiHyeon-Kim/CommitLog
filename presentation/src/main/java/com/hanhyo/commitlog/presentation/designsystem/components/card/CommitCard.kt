package com.hanhyo.commitlog.presentation.designsystem.components.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitAnalysis
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.DifficultyLevel
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun CommitCard(
    commit: Commit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(Dimensions.CardRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.CardPadding),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // AI Mood 이모지
            Text(
                text = commit.analysis?.mood?.emoji ?: "📝",
                fontSize = Dimensions.IconXLarge.value.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // 콘텐츠
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 제목
                Text(
                    text = commit.title.value,
                    style = CommitLogTheme.typography.titleLarge,
                    color = CommitLogTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 학습 내용 미리보기
                Text(
                    text = commit.learnedToday.value,
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // AI Mood + 시간
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    commit.analysis?.let { analysis ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CommitLogTheme.colors.primaryLight.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = analysis.mood.displayNameKo,
                                style = CommitLogTheme.typography.labelSmall,
                                color = CommitLogTheme.colors.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = formatTime(commit.createdAt),
                        style = CommitLogTheme.typography.bodySmall,
                        color = CommitLogTheme.colors.textTertiary
                    )
                }
            }
        }
    }
}

/**
 * 시간 포맷 (오전 10:45)
 */
private fun formatTime(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val time = instant.atZone(ZoneId.systemDefault()).toLocalTime()
    val hour = if (time.hour == 0) 12 else if (time.hour > 12) time.hour - 12 else time.hour
    val amPm = if (time.hour < 12) "오전" else "오후"
    return "$amPm ${hour}:${time.minute.toString().padStart(2, '0')}"
}

@Preview(name = "CommitCard - Light")
@Composable
private fun CommitCardLightPreview() {
    CommitLogTheme {
        val mockCommit = Commit(
            id = CommitId(1),
            date = LocalDate.now(),
            title = CommitTitle("Jetpack Compose State 학습"),
            learnedToday = LearnedContent("remember와 mutableStateOf의 차이점을 공부했습니다. State Hoisting 패턴도 이해했어요."),
            difficulties = null,
            tomorrowPlan = null,
            tags = setOf(LearningTag("compose")),
            analysis = CommitAnalysis(
                mood = AIMood.FOCUSED,
                moodScore = 85,
                difficultyLevel = DifficultyLevel.NORMAL,
                comment = "집중해서 학습하셨네요!"
            ),
            analysisStatus = com.hanhyo.commitlog.domain.model.AnalysisStatus.COMPLETED,
            isDraft = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )

        CommitCard(
            commit = mockCommit,
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
@Preview(name = "CommitCard - Dark")
@Composable
private fun CommitCardDarkPreview() {
    CommitLogTheme(darkTheme = true) {
        val mockCommit = Commit(
            id = CommitId(1),
            date = LocalDate.now(),
            title = CommitTitle("Jetpack Compose State 학습"),
            learnedToday = LearnedContent("remember와 mutableStateOf의 차이점을 공부했습니다. State Hoisting 패턴도 이해했어요."),
            difficulties = null,
            tomorrowPlan = null,
            tags = setOf(LearningTag("compose")),
            analysis = CommitAnalysis(
                mood = AIMood.FOCUSED,
                moodScore = 85,
                difficultyLevel = DifficultyLevel.NORMAL,
                comment = "집중해서 학습하셨네요!"
            ),
            analysisStatus = com.hanhyo.commitlog.domain.model.AnalysisStatus.COMPLETED,
            isDraft = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )

        CommitCard(
            commit = mockCommit,
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
