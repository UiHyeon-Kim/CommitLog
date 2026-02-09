package com.hanhyo.commitlog.presentation.designsystem.components.bar.model

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

sealed class AppBarConfig(
    open val title: String,
    val navItem: AppBarNavItem = AppBarNavItem.None,
    val actions: @Composable RowScope.() -> Unit = {}
) {
    data object Statistics : AppBarConfig(
        title = "통계",
        navItem = AppBarNavItem.None
    )

    data object Review : AppBarConfig(
        title = "월간 회고",
        navItem = AppBarNavItem.None
    )

    data class Write(
        val onBack: () -> Unit
    ) : AppBarConfig(
        title = "기록하기",
        navItem = AppBarNavItem.Back(onClick = onBack)
    )

    data class Detail(
        override val title: String,
        val onBack: () -> Unit
    ) : AppBarConfig(
        title = title,
        navItem = AppBarNavItem.Back(onClick = onBack)
    )
}

@Stable
sealed interface AppBarNavItem {

    data class Back(val onClick: () -> Unit) : AppBarNavItem

    data object None : AppBarNavItem
}
