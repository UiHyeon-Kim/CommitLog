package com.hanhyo.commitlog.presentation.designsystem.components.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.AppBarNavItem
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitLogTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    navItem: AppBarNavItem = AppBarNavItem.None,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            if (navItem is AppBarNavItem.Back) {
                IconButton(
                    onClick = navItem.onClick,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_left_arrow),
                        contentDescription = "뒤로가기",
                        tint = CommitLogTheme.colors.textPrimary
                    )
                }
            }
        },
        title = {
            Text(
                text = title,
                style = CommitLogTheme.typography.headlineSmall,
                color = CommitLogTheme.colors.textPrimary
            )
        },
        actions = actions,
        modifier = modifier.height(CommitLogTheme.dimens.buttonHeightLarge)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitLogHomeAppBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = "CommitLog",
                style = CommitLogTheme.typography.headlineMedium,
                color = CommitLogTheme.colors.textPrimary
            )
        },
        actions = actions,
        modifier = modifier.height(CommitLogTheme.dimens.buttonHeightLarge)
    )
}

@Preview
@Composable
private fun CommitLogTopAppBarPreview() {
    CommitLogTheme {
        CommitLogTopAppBar(
            title = "안녕",
            navItem = AppBarNavItem.Back(onClick = {}),
            actions = {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.ic_review),
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@Preview
@Composable
private fun CommitLogHomeAppBarPreview() {
    CommitLogTheme {
        CommitLogHomeAppBar(
            actions = {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.ic_review),
                        contentDescription = null
                    )
                }
            }
        )
    }
}
