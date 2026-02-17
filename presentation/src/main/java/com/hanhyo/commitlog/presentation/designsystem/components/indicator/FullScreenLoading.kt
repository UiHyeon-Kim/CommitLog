package com.hanhyo.commitlog.presentation.designsystem.components.indicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@Composable
fun FullScreenLoading(
    message: String = "로딩 중...",
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = CommitLogTheme.colors.primary
            )

            Text(
                text = message,
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary
            )
        }
    }
}
