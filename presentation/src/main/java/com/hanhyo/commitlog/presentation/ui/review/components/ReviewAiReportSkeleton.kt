package com.hanhyo.commitlog.presentation.ui.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.components.indicator.SkeletonItem
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.valentinilk.shimmer.shimmer

/**
 * AI 리포트 카드의 스켈레톤 UI
 */
@Composable
fun ReviewAiReportSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.CardRadius))
            .shimmer()
            .background(CommitLogTheme.colors.surface)
            .padding(Dimensions.CardPadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkeletonItem(width = 24.dp, height = 24.dp)
                SkeletonItem(width = 140.dp, height = 24.dp)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                SkeletonItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                SkeletonItem(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                )
                SkeletonItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                SkeletonItem(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewAiReportSkeletonPreview() {
    CommitLogTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReviewAiReportSkeleton()
        }
    }
}
