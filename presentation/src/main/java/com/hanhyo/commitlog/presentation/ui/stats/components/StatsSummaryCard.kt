package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun StatsSummaryCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
        ) {
            Text(
                text = icon,
                style = CommitLogTheme.typography.titleMedium
            )
            Text(
                text = title,
                style = CommitLogTheme.typography.bodySmall,
                color = CommitLogTheme.colors.textSecondary
            )
            Text(
                text = value,
                style = CommitLogTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = CommitLogTheme.colors.textPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsSummaryCardPreview() {
    CommitLogTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
            StatsSummaryCard(
                title = "총 집중 시간",
                value = "12.5 시간",
                icon = "⏱️",
                modifier = Modifier.weight(1f)
            )
            StatsSummaryCard(
                title = "주간 커밋",
                value = "18 회",
                icon = "commit",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
