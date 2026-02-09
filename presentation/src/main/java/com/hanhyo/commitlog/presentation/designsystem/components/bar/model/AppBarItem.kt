package com.hanhyo.commitlog.presentation.designsystem.components.bar.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

sealed class AppBarItem(
    @get:StringRes val title: Int,
    val navItem: AppBarNavItem,
    val actions: @Composable () -> Unit = {},
)

@Stable
sealed interface AppBarNavItem {

    data class Back(val onClick: () -> Unit) : AppBarNavItem

    data object None : AppBarNavItem
}
