package com.hanhyo.commitlog.presentation.designsystem.theme.dimension

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // 태그, 작은 버튼
    small = RoundedCornerShape(8.dp),        // 일반 버튼
    medium = RoundedCornerShape(12.dp),      // 카드, 입력 필드
    large = RoundedCornerShape(16.dp),       // 큰 카드
    extraLarge = RoundedCornerShape(28.dp)   // FAB, 다이얼로그
)
