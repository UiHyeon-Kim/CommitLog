package com.hanhyo.commitlog.presentation.designsystem.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.domain.model.CommitAnalysis
import com.hanhyo.commitlog.domain.model.DifficultyLevel
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun EmotionAnalysisCard(
    analysis: CommitAnalysis,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.primaryLight.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(Dimensions.CardRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 헤더
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🤖",
                    fontSize = 20.sp
                )
                Text(
                    text = "AI 인사이트",
                    style = CommitLogTheme.typography.titleMedium,
                    color = CommitLogTheme.colors.textPrimary
                )
            }
            
            // Mood + 점수
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mood 이모지
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CommitLogTheme.colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = analysis.mood.emoji,
                        fontSize = 36.sp
                    )
                }
                
                // 정보
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = analysis.mood.displayNameKo,
                        style = CommitLogTheme.typography.headlineSmall,
                        color = CommitLogTheme.colors.textPrimary
                    )
                    
                    Text(
                        text = "학습 만족도: ${analysis.moodScore}/100",
                        style = CommitLogTheme.typography.bodyMedium,
                        color = CommitLogTheme.colors.textSecondary
                    )
                    
                    Text(
                        text = "난이도: ${analysis.difficultyLevel.displayName}",
                        style = CommitLogTheme.typography.bodyMedium,
                        color = CommitLogTheme.colors.textSecondary
                    )
                }
            }
            
            // 코멘트
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = CommitLogTheme.colors.surface
            ) {
                Text(
                    text = "\"${analysis.comment}\"",
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun EmotionAnalysisCardLightPreview() {
    CommitLogTheme {
        EmotionAnalysisCard(
            analysis = CommitAnalysis(
                mood = AIMood.PRODUCTIVE,
                moodScore = 92,
                difficultyLevel = DifficultyLevel.NORMAL,
                comment = "오늘 분석과 베스트에 테스터 하신 적이 좋습니다! 출력 지속 바랍니다."
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun EmotionAnalysisCardDarkPreview() {
    CommitLogTheme(darkTheme = true) {
        EmotionAnalysisCard(
            analysis = CommitAnalysis(
                mood = AIMood.PRODUCTIVE,
                moodScore = 92,
                difficultyLevel = DifficultyLevel.NORMAL,
                comment = "오늘 분석과 베스트에 테스터 하신 적이 좋습니다! 출력 지속 바랍니다."
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
