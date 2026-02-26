package com.hanhyo.commitlog.presentation.designsystem.components.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.valentinilk.shimmer.shimmer

/**
 * 스켈레톤 UI를 구성을 위한 공통 컴포넌트입니다.
 *
 * @param modifier 컴포저블에 적용할 모디파이어
 * @param width 고정 너비 (null일 경우 modifier 설정에 따름)
 * @param height 고정 높이 (null일 경우 modifier 설정에 따름)
 * @param shape 모서리 모양 (기본값: Dimensions.CardRadius)
 * @param color 배경 색상 (기본값: CommitLogTheme.colors.surfaceVariant)
 */
@Composable
fun SkeletonItem(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
    shape: Shape = RoundedCornerShape(Dimensions.CardRadius),
    color: Color = CommitLogTheme.colors.surfaceVariant,
) {
    val sizeModifier = if (width != null && height != null) {
        Modifier.size(width = width, height = height)
    } else if (height != null) {
        Modifier.height(height)
    } else if (width != null) {
        Modifier.width(width)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(sizeModifier)
            .clip(shape)
            .shimmer()
            .background(color)
    )
}
