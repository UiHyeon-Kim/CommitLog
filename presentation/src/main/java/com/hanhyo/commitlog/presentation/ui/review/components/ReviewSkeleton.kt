package com.hanhyo.commitlog.presentation.ui.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.valentinilk.shimmer.shimmer

@Composable
fun ReviewSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        // 1. Summary Cards Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            SkeletonItem(modifier = Modifier.weight(1f).height(80.dp))
            SkeletonItem(modifier = Modifier.weight(1f).height(80.dp))
        }

        // AI Report Card Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimensions.CardRadius))
                .shimmer()
                .background(CommitLogTheme.colors.surfaceVariant)
                .padding(Dimensions.CardPadding)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SkeletonItem(width = 24.dp, height = 24.dp)
                    SkeletonItem(width = 140.dp, height = 24.dp)
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonItem(modifier = Modifier.fillMaxWidth().height(16.dp))
                    SkeletonItem(modifier = Modifier.fillMaxWidth().height(16.dp))
                    SkeletonItem(modifier = Modifier.fillMaxWidth(0.8f).height(16.dp))
                    SkeletonItem(modifier = Modifier.fillMaxWidth().height(16.dp))
                    SkeletonItem(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SkeletonItem(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
) {
    val sizeModifier = if (width != null && height != null) {
        Modifier.size(width = width, height = height)
    } else if (height != null) {
        Modifier.height(height)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(sizeModifier)
            .clip(RoundedCornerShape(Dimensions.CardRadius))
            .shimmer()
            .background(CommitLogTheme.colors.surfaceVariant)
    )
}

@Preview(showBackground = true)
@Composable
private fun ReviewSkeletonPreview() {
    CommitLogTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReviewSkeleton()
        }
    }
}
