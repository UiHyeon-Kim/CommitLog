package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@Composable
fun MoodProgressBar(
    label: String,
    percentage: Float,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = CommitLogTheme.typography.bodySmall,
                color = CommitLogTheme.colors.textSecondary
            )
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = CommitLogTheme.typography.labelSmall,
                color = CommitLogTheme.colors.textPrimary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small)
                .background(CommitLogTheme.colors.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.small)
                    .background(color)
            )
        }
    }
}

@Preview
@Composable
private fun MoodProgressBarPreview() {
    CommitLogTheme {
        MoodProgressBar(
            label = "Positive",
            percentage = 0.75f,
            color = CommitLogTheme.colors.success
        )
    }
}
