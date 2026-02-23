package com.hanhyo.commitlog.presentation.ui.review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun ReviewAiReportCard(
    title: String,
    emoji: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.surface,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = emoji, fontSize = 18.sp)
                Text(
                    text = title,
                    style = CommitLogTheme.typography.titleMedium,
                    color = CommitLogTheme.colors.textPrimary,
                )
            }

            Text(
                text = content,
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary,
                lineHeight = 22.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewAiReportCardPreview() {
    CommitLogTheme {
        ReviewAiReportCard(
            title = "AI 분석 리포트",
            emoji = "🤖",
            content = "이번 달에는 특히 Jetpack Compose 학습에 많은 시간을 쏟으셨네요. 꾸준한 노력이 돋보입니다!"
        )
    }
}
