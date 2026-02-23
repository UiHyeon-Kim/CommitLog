package com.hanhyo.commitlog.presentation.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun CircularIndicatorItem(
    score: Int,
    text: String? = null,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(64.dp)
        ) {
            CircularProgressIndicator(
                progress = { if (text == null) score / 100f else 1f },
                modifier = Modifier.fillMaxSize(),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
            )
            if (text == null) {
                Text(
                    text = "$score%",
                    style = CommitLogTheme.typography.headlineSmall,
                    color = CommitLogTheme.colors.textPrimary
                )
            } else {
                Text(
                    text = text,
                    style = CommitLogTheme.typography.headlineLarge,
                    color = CommitLogTheme.colors.textPrimary
                )
            }
        }
        Text(
            text = label,
            style = CommitLogTheme.typography.bodySmall,
            color = CommitLogTheme.colors.textSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CircularIndicatorItemPreview() {
    CommitLogTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularIndicatorItem(
                score = 85,
                label = "학습 확신도",
                color = CommitLogTheme.colors.primary
            )
            CircularIndicatorItem(
                score = 0,
                text = "🤔",
                label = "감정",
                color = CommitLogTheme.colors.accentPink
            )
        }
    }
}
