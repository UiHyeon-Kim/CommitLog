package com.hanhyo.commitlog.presentation.designsystem.components.Button

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun CommitLogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    containerColor: Color = CommitLogTheme.colors.primary,
    contentColor: Color = Color.White,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = Dimensions.ButtonHeightMedium),
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = CommitLogTheme.colors.textDisabled,
            disabledContentColor = Color.White
        ),
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = text,
            style = CommitLogTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommitLogButtonPreview() {
    CommitLogTheme {
        CommitLogButton(
            text = "CommitLogButton",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommitLogButtonDisabledPreview() {
    CommitLogTheme {
        CommitLogButton(
            text = "CommitLogButton",
            onClick = {},
            enabled = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommitLogButtonLoadingPreview() {
    CommitLogTheme {
        CommitLogButton(
            text = "CommitLogButton",
            onClick = {},
            loading = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommitLogButtonLoadingDarkPreview() {
    CommitLogTheme(darkTheme = true) {
        CommitLogButton(
            text = "CommitLogButton",
            onClick = {},
            loading = true
        )
    }
}
