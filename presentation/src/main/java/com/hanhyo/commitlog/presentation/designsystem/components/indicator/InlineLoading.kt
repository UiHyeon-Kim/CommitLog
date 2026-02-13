package com.hanhyo.commitlog.presentation.designsystem.components.indicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@Composable
fun InlineLoading(
    message: String = "로딩 중...",
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = CommitLogTheme.colors.primary,
            strokeWidth = 2.dp
        )

        Text(
            text = message,
            style = CommitLogTheme.typography.bodyMedium,
            color = CommitLogTheme.colors.textSecondary
        )
    }
}

@Preview
@Composable
private fun LoadingIndicatorPreview() {
    CommitLogTheme {
        InlineLoading(message = "AI 분석 중...")
    }
}
