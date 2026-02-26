package com.hanhyo.commitlog.presentation.ui.review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun SummaryStatCard(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.surface,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = emoji, fontSize = 14.sp)
                Text(
                    text = label,
                    style = CommitLogTheme.typography.bodyLarge,
                    color = CommitLogTheme.colors.textSecondary,
                )
            }
            Text(
                text = value,
                style = CommitLogTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = CommitLogTheme.colors.textPrimary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryStatCardPreview() {
    CommitLogTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryStatCard(
                emoji = "📝",
                label = "총 기록 수",
                value = "20",
                modifier = Modifier.weight(1f)
            )
            SummaryStatCard(
                emoji = "😄",
                label = "평균 감정",
                value = "뿌듯함",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
