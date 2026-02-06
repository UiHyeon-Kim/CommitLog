package com.hanhyo.commitlog.presentation.designsystem.components.bar.model

import androidx.compose.runtime.Stable

@Stable
sealed interface AppBarNavItem {

    data class Back(val onClick: () -> Unit) : AppBarNavItem

    data object None : AppBarNavItem
}
