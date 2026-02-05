package com.hanhyo.commitlog.presentation.designsystem.theme.dimension

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.hanhyo.commitlog.presentation.designsystem.theme.color.LocalCommitLogColors

object Gradients {
    val primaryGradient: Brush
        @Composable
        get() = Brush.Companion.horizontalGradient(
            colors = listOf(
                LocalCommitLogColors.current.primaryVariant,
                LocalCommitLogColors.current.primary
            )
        )

    val primaryVerticalGradient: Brush
        @Composable
        get() = Brush.Companion.verticalGradient(
            colors = listOf(
                LocalCommitLogColors.current.primaryVariant,
                LocalCommitLogColors.current.primary
            )
        )

    val moodGradient: Brush
        @Composable
        get() = Brush.Companion.linearGradient(
            colors = listOf(
                LocalCommitLogColors.current.primary,
                LocalCommitLogColors.current.accentPurple,
                LocalCommitLogColors.current.accentPink
            )
        )

    val cardGradient: Brush
        @Composable
        get() = Brush.Companion.horizontalGradient(
            colors = listOf(
                LocalCommitLogColors.current.primaryVariant.copy(alpha = 0.3f),
                LocalCommitLogColors.current.primary.copy(alpha = 0.1f)
            )
        )
}
