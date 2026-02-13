package com.hanhyo.commitlog.presentation.designsystem.components.Button

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun CommitLogOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = Dimensions.ButtonHeightMedium),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = CommitLogTheme.colors.primary
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(CommitLogTheme.colors.primary)
        ),
        shape = RoundedCornerShape(Dimensions.ButtonRadius)
    ) {
        Text(
            text = text,
            style = CommitLogTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CommitLogOutlinedButtonPreview() {
    CommitLogTheme {
        CommitLogOutlinedButton(
            text = "CommitLogOutlinedButton",
            onClick = {}
        )
    }
}
