package com.hanhyo.commitlog.presentation.ui.write.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.components.textfield.CommitLogTextField
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun InputSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary
            )
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun InputSectionPreview() {
    CommitLogTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            InputSection(title = "제목") {
                CommitLogTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "제목을 입력하세요",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
