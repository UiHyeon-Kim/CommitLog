package com.hanhyo.commitlog.presentation.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun ContentSectionWithColorBar(
    title: String,
    content: String,
    barColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)) {
        Text(
            text = title,
            style = CommitLogTheme.typography.titleLarge,
            color = barColor
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)) {
            Surface(
                color = barColor.copy(alpha = 0.5f),
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(),
                shape = MaterialTheme.shapes.small
            ) {}

            Spacer(modifier = Modifier.width(Dimensions.SpacingMedium))

            Text(
                text = content,
                style = CommitLogTheme.typography.bodyLarge,
                color = CommitLogTheme.colors.textPrimary,
                modifier = Modifier.padding(vertical = Dimensions.SpacingXSmall)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentSectionWithColorBarPreview() {
    CommitLogTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ContentSectionWithColorBar(
                title = "오늘 배운 점",
                content = "remember와 mutableStateOf의 차이점을 공부했습니다.",
                barColor = CommitLogTheme.colors.primary
            )
            ContentSectionWithColorBar(
                title = "어려운 점",
                content = "State Hoisting 개념이 어려웠습니다.",
                barColor = CommitLogTheme.colors.accentOrange
            )
        }
    }
}
