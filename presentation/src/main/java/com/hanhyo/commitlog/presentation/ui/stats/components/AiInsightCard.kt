package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun AiInsightCard(
    insight: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4A148C)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Column(
                modifier = Modifier.padding(Dimensions.CardPadding),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✨", fontSize = 20.sp)
                    Text(
                        text = "AI 인사이트",
                        style = CommitLogTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
                Text(
                    text = insight,
                    style = CommitLogTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiInsightCardPreview() {
    CommitLogTheme {
        AiInsightCard(
            insight = "꾸준한 기록이 성장의 밑거름이 됩니다. 오늘도 화이팅하세요!"
        )
    }
}
