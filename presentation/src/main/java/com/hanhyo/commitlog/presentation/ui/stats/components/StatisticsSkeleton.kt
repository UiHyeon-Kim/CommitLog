package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.components.indicator.SkeletonItem
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.valentinilk.shimmer.shimmer

@Composable
fun StatisticsSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        // 1. Header Skeleton
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SkeletonItem(width = 120.dp, height = 28.dp)
            SkeletonItem(width = 200.dp, height = 16.dp)
        }

        // 2. Summary Cards Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            SkeletonItem(modifier = Modifier
                .weight(1f)
                .height(100.dp))
            SkeletonItem(modifier = Modifier
                .weight(1f)
                .height(100.dp))
        }

        // 3. Main Chart Skeleton
        SkeletonItem(modifier = Modifier
            .fillMaxWidth()
            .height(240.dp))

        // 4. Additional Content Skeleton
        Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
            SkeletonItem(width = 140.dp, height = 20.dp)
            SkeletonItem(modifier = Modifier
                .fillMaxWidth()
                .height(120.dp))
        }

        AiInsightSkeleton()
    }
}

@Composable
private fun AiInsightSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.CardRadius))
            .shimmer()
            .background(CommitLogTheme.colors.primaryLight.copy(alpha = 0.1f))
            .padding(Dimensions.CardPadding)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CommitLogTheme.colors.primary.copy(alpha = 0.2f))
            )
            SkeletonItem(modifier = Modifier
                .weight(1f)
                .height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsSkeletonPreview() {
    CommitLogTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            StatisticsSkeleton()
        }
    }
}
