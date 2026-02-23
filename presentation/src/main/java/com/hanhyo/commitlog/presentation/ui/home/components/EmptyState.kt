package com.hanhyo.commitlog.presentation.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun EmptyState(
    emoji: String,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = emoji,
                fontSize = Dimensions.EmptyStateEmojiSize
            )

            Text(
                text = title,
                style = CommitLogTheme.typography.headlineSmall,
                color = CommitLogTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )

            action?.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    CommitLogTheme {
        EmptyState(
            emoji = "📝",
            title = "첫 커밋을 작성해보세요!",
            message = "오늘 배운 내용을 기록하고\nAI의 분석을 받아보세요"
        )
    }
}
