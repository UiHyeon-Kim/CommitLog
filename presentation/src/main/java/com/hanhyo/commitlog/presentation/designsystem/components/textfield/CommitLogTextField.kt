package com.hanhyo.commitlog.presentation.designsystem.components.textfield

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun CommitLogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = placeholder,
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textTertiary
            )
        },
        enabled = enabled,
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = CommitLogTheme.typography.bodyMedium.copy(
            color = CommitLogTheme.colors.textPrimary
        ),
        shape = RoundedCornerShape(Dimensions.TextFieldRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CommitLogTheme.colors.primary,
            unfocusedBorderColor = CommitLogTheme.colors.border,
            disabledBorderColor = CommitLogTheme.colors.borderVariant,
            focusedContainerColor = CommitLogTheme.colors.surface,
            unfocusedContainerColor = CommitLogTheme.colors.surface,
            disabledContainerColor = CommitLogTheme.colors.surfaceVariant,
            cursorColor = CommitLogTheme.colors.primary
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Preview(showBackground = true)
@Composable
fun CommitLogTextFieldPreview() {
    CommitLogTheme {
        CommitLogTextField(
            value = "CommitLogTextField",
            onValueChange = {}
        )
    }
}
