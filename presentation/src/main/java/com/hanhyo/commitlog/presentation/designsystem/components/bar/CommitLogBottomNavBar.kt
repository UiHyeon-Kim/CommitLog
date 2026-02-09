package com.hanhyo.commitlog.presentation.designsystem.components.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.BottomNavItem
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun CommitLogBottomNavBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    Column {
        HorizontalDivider(thickness = 1.dp, color = CommitLogTheme.colors.border)

        Row(
            modifier = modifier
                .selectableGroup()
                .fillMaxWidth()
                .background(CommitLogTheme.colors.surface)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.entries.forEach { item ->
                val isSelected = currentDestination?.hasRoute(item.tabRouteClass) ?: false

                CommitLogNavBarContent(
                    selected = isSelected,
                    bottomNavItem = item,
                    onClick = {
                        navController.navigate(item.tabRoute) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun RowScope.CommitLogNavBarContent(
    selected: Boolean,
    onClick: () -> Unit,
    bottomNavItem: BottomNavItem,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) CommitLogTheme.colors.primary
    else CommitLogTheme.colors.textDisabled

    Column(
        modifier = modifier
            .weight(1f)
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = { if (!selected) onClick() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            )
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = bottomNavItem.iconResId),
            contentDescription = stringResource(bottomNavItem.labelResId),
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )

        Text(
            text = stringResource(bottomNavItem.labelResId),
            style = CommitLogTheme.typography.titleSmall,
            color = contentColor,
        )
    }
}

@Preview
@Composable
private fun CommitLogBottomNavBarPreview() {
    CommitLogTheme {
        CommitLogBottomNavBar(
            navController = NavHostController(LocalContext.current),
            currentDestination = null,
        )
    }
}
