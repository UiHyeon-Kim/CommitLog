package com.hanhyo.commitlog.presentation.ui.home.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@Composable
fun CommitLogFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = CommitLogTheme.colors.primary,
        contentColor = CommitLogTheme.colors.surface,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = "작성하기",
            tint = CommitLogTheme.colors.surface
        )
    }
}

@Preview
@Composable
private fun CommitLogFloatingActionButtonPreview() {
    CommitLogTheme {
        CommitLogFloatingActionButton {}
    }
}
