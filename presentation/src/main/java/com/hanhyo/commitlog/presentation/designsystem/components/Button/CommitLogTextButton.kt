package com.hanhyo.commitlog.presentation.designsystem.components.Button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@Composable
fun CommitLogTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = CommitLogTheme.colors.primary,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = color
        )
    ) {
        Text(
            text = text,
            style = CommitLogTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CommitLogTextButtonPreview() {
    CommitLogTheme {
        CommitLogTextButton(
            text = "CommitLogTextButton",
            onClick = {}
        )
    }
}
