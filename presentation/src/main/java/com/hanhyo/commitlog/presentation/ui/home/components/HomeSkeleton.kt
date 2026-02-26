package com.hanhyo.commitlog.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun HomeSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.SpacingLarge, vertical = Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        // 1. Date Header Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SkeletonItem(width = 80.dp, height = 24.dp)
            SkeletonItem(width = 40.dp, height = 16.dp)
        }

        // 2. Commit Card Skeletons
        repeat(3) {
            HomeCardSkeleton()
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

        // 3. Another Date Header Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SkeletonItem(width = 80.dp, height = 24.dp)
        }

        // 4. More Card Skeletons
        repeat(2) {
            HomeCardSkeleton()
        }
    }
}

@Composable
private fun HomeCardSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(Dimensions.CardRadius))
            .shimmer()
            .background(CommitLogTheme.colors.surfaceVariant)
            .padding(Dimensions.CardPadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Title and Mood
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonItem(modifier = Modifier
                    .weight(1f)
                    .height(24.dp))
                SkeletonItem(width = 24.dp, height = 24.dp)
            }

            // Learned Content Preview (2 lines)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SkeletonItem(modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp))
                SkeletonItem(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) {
                    SkeletonItem(width = 50.dp, height = 20.dp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSkeletonPreview() {
    CommitLogTheme {
        HomeSkeleton()
    }
}
