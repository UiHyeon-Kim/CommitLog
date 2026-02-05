package com.hanhyo.commitlog.presentation.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.hanhyo.commitlog.presentation.designsystem.theme.color.CommitLogColors
import com.hanhyo.commitlog.presentation.designsystem.theme.color.DarkColors
import com.hanhyo.commitlog.presentation.designsystem.theme.color.LightColors
import com.hanhyo.commitlog.presentation.designsystem.theme.color.LocalCommitLogColors
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.LocalCommitLogDimens
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Shapes
import com.hanhyo.commitlog.presentation.designsystem.theme.typography.CommitLogTypography
import com.hanhyo.commitlog.presentation.designsystem.theme.typography.LocalCommitLogTypography

private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.primary,
    onPrimary = Color.White,
    primaryContainer = DarkColors.primaryVariant,
    onPrimaryContainer = DarkColors.primaryLight,

    secondary = DarkColors.accentPurple,
    onSecondary = Color.White,

    tertiary = DarkColors.accentOrange,
    onTertiary = Color.White,

    background = DarkColors.background,
    onBackground = DarkColors.textPrimary,

    surface = DarkColors.surface,
    onSurface = DarkColors.textPrimary,
    surfaceVariant = DarkColors.surfaceVariant,
    onSurfaceVariant = DarkColors.textSecondary,

    outline = DarkColors.border,
    outlineVariant = DarkColors.borderVariant,

    error = DarkColors.error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightColors.primary,
    onPrimary = Color.White,
    primaryContainer = LightColors.primaryLight,
    onPrimaryContainer = LightColors.primaryVariant,

    secondary = LightColors.accentPurple,
    onSecondary = Color.White,

    tertiary = LightColors.accentOrange,
    onTertiary = Color.White,

    background = LightColors.background,
    onBackground = LightColors.textPrimary,

    surface = LightColors.surface,
    onSurface = LightColors.textPrimary,
    surfaceVariant = LightColors.surfaceVariant,
    onSurfaceVariant = LightColors.textSecondary,

    outline = LightColors.border,
    outlineVariant = LightColors.borderVariant,

    error = LightColors.error,
    onError = Color.White
)

@Composable
fun CommitLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = LocalCommitLogTypography.current
    val dimens = LocalCommitLogDimens.current

    CompositionLocalProvider(
        LocalCommitLogColors provides colors,
        LocalCommitLogTypography provides typography,
        LocalCommitLogDimens provides dimens,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = materialTypography(typography),
            shapes = Shapes,
            content = content
        )
    }
}

@Composable
private fun materialTypography(commitLogTypography: CommitLogTypography): Typography {
    return Typography(
        displayLarge = commitLogTypography.displayLarge,
        headlineLarge = commitLogTypography.headlineLarge,
        headlineMedium = commitLogTypography.headlineMedium,
        headlineSmall = commitLogTypography.headlineSmall,
        titleLarge = commitLogTypography.titleLarge,
        titleMedium = commitLogTypography.titleMedium,
        titleSmall = commitLogTypography.titleSmall,
        bodyLarge = commitLogTypography.bodyLarge,
        bodyMedium = commitLogTypography.bodyMedium,
        bodySmall = commitLogTypography.bodySmall,
        labelLarge = commitLogTypography.labelLarge,
        labelMedium = commitLogTypography.labelMedium,
        labelSmall = commitLogTypography.labelSmall
    )
}

object CommitLogTheme {
    val colors: CommitLogColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCommitLogColors.current

    val typography: CommitLogTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalCommitLogTypography.current

    val dimens: Dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalCommitLogDimens.current
}
