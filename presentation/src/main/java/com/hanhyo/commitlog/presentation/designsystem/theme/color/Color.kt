package com.hanhyo.commitlog.presentation.designsystem.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CommitLogColors(
    val primary: Color,
    val primaryVariant: Color,
    val primaryLight: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val accentOrange: Color,
    val accentPurple: Color,
    val accentPink: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val border: Color,
    val borderVariant: Color,
    val success: Color,
    val error: Color,
    val warning: Color,
    val isLight: Boolean
)

val DarkColors = CommitLogColors(
    primary = Color(0xFF4169E1),
    primaryVariant = Color(0xFF1E3A8A),
    primaryLight = Color(0xFF6B8EF5),
    background = Color(0xFF0A0E1A),
    surface = Color(0xFF141824),
    surfaceVariant = Color(0xFF1A1F2E),
    accentOrange = Color(0xFFFFa500),
    accentPurple = Color(0xFF9333EA),
    accentPink = Color(0xFFEC4899),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFD1D5DB),
    textTertiary = Color(0xFF9CA3AF),
    textDisabled = Color(0xFF6B7280),
    border = Color(0xFF374151),
    borderVariant = Color(0xFF1F2937),
    success = Color(0xFF10B981),
    error = Color(0xFFEF4444),
    warning = Color(0xFFF59E0B),
    isLight = false
)

val LightColors = CommitLogColors(
    primary = Color(0xFF4169E1),
    primaryVariant = Color(0xFF1E3A8A),
    primaryLight = Color(0xFF6B8EF5),
    background = Color(0xFFF9FAFB),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF3F4F6),
    accentOrange = Color(0xFFFFa500),
    accentPurple = Color(0xFF9333EA),
    accentPink = Color(0xFFEC4899),
    textPrimary = Color(0xFF111827),
    textSecondary = Color(0xFF4B5563),
    textTertiary = Color(0xFF6B7280),
    textDisabled = Color(0xFF9CA3AF),
    border = Color(0xFFE5E7EB),
    borderVariant = Color(0xFFD1D5DB),
    success = Color(0xFF10B981),
    error = Color(0xFFEF4444),
    warning = Color(0xFFF59E0B),
    isLight = true
)

val LocalCommitLogColors = staticCompositionLocalOf { LightColors }
