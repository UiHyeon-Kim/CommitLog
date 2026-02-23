package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.stats.model.ProductiveDay

@Composable
fun ProductiveDayItem(
    day: ProductiveDay,
    rank: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CommitLogTheme.colors.surfaceVariant.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
            .padding(Dimensions.SpacingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (rank == 1) CommitLogTheme.colors.primary else CommitLogTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                style = CommitLogTheme.typography.titleMedium,
                color = if (rank == 1) Color.White else CommitLogTheme.colors.textSecondary
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.day,
                    style = CommitLogTheme.typography.titleMedium,
                    color = CommitLogTheme.colors.textPrimary
                )
                if (day.isTop) {
                    Text(
                        text = "High Score",
                        style = CommitLogTheme.typography.labelSmall,
                        color = CommitLogTheme.colors.primary
                    )
                }
            }
            Text(
                text = day.description,
                style = CommitLogTheme.typography.bodySmall,
                color = CommitLogTheme.colors.textSecondary
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${day.count}",
                style = CommitLogTheme.typography.titleMedium,
                color = CommitLogTheme.colors.textPrimary
            )
            Text(
                text = "commits",
                style = CommitLogTheme.typography.labelSmall,
                color = CommitLogTheme.colors.textTertiary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductiveDayItemPreview() {
    CommitLogTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductiveDayItem(
                day = ProductiveDay(
                    day = "월요일",
                    count = 5,
                    isTop = true,
                    description = "가장 많은 커밋을 기록했어요!"
                ),
                rank = 1
            )
            ProductiveDayItem(
                day = ProductiveDay(
                    day = "수요일",
                    count = 4,
                    isTop = false,
                    description = "꾸준히 기록하고 있어요."
                ),
                rank = 2
            )
        }
    }
}
