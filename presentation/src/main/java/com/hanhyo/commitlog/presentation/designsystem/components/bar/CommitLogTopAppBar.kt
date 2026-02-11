package com.hanhyo.commitlog.presentation.designsystem.components.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.AppBarNavItem
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitLogTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    navItem: AppBarNavItem = AppBarNavItem.None,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
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
            modifier = Modifier
                .statusBarsPadding()
                .height(Dimensions.TopAppBarHeight),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = CommitLogTheme.colors.background
            )
        )
        HorizontalDivider(thickness = 0.3.dp, color = CommitLogTheme.colors.border)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitLogHomeAppBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
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
            modifier = Modifier
                .statusBarsPadding()
                .height(Dimensions.TopAppBarHeight),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = CommitLogTheme.colors.background
            )
        )
        HorizontalDivider(thickness = 0.3.dp, color = CommitLogTheme.colors.border)
    }
}

@Preview
@Composable
private fun CommitLogTopAppBarPreview() {
    CommitLogTheme {
        CommitLogTopAppBar(
            title = "2026년 2월 09일",
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
