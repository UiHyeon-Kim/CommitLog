package com.hanhyo.commitlog.presentation.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun ContentSection(
    title: String,
    content: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)) {
        Text(
            text = title,
            style = CommitLogTheme.typography.titleMedium,
            color = CommitLogTheme.colors.primary
        )

        Text(
            text = content,
            style = CommitLogTheme.typography.bodyLarge,
            color = CommitLogTheme.colors.textPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentSectionPreview() {
    CommitLogTheme {
        ContentSection(
            title = "오늘 배운 점",
            content = "remember와 mutableStateOf의 차이점을 공부했습니다.",
        )
    }
}
