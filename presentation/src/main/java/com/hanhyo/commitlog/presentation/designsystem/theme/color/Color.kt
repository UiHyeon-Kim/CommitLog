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
    primary = Color(0xFF5C7CFA), // More vibrant blue
    primaryVariant = Color(0xFF364FC7),
    primaryLight = Color(0xFF748FFC),
    background = Color(0xFF0F1116), // Deep charcoal/black
    surface = Color(0xFF1A1D23), // Slightly lighter for cards
    surfaceVariant = Color(0xFF252932), // Lighter for embedded cards
    accentOrange = Color(0xFFFF922B),
    accentPurple = Color(0xFF9775FA), // Soft neon purple
    accentPink = Color(0xFFFAA2C1), // Soft neon pink
    textPrimary = Color(0xFFF8F9FA), // Almost white
    textSecondary = Color(0xFFCED4DA), // Light gray
    textTertiary = Color(0xFF868E96), // Muted gray
    textDisabled = Color(0xFF495057),
    border = Color(0xFF2C2E33), // Subtle border
    borderVariant = Color(0xFF343A40),
    success = Color(0xFF51CF66),
    error = Color(0xFFFF6B6B),
    warning = Color(0xFFFCC419),
    isLight = false
)

val LightColors = CommitLogColors(
    primary = Color(0xFF4169E1),
    primaryVariant = Color(0xFF1E3A8A),
    primaryLight = Color(0xFF6B8EF5),
    background = Color(0xFFF9FAFB),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF3F4F6),
    accentOrange = Color(0xFFFFA500),
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
