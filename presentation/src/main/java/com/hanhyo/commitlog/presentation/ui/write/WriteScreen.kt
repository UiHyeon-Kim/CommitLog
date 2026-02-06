package com.hanhyo.commitlog.presentation.ui.write

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@Composable
fun WriteScreen(onBack: () -> Boolean) {
    WriteContent()
}

@Composable
fun WriteContent(modifier: Modifier = Modifier) {

}

@Preview(showBackground = true)
@Composable
private fun WritePreview() {
    CommitLogTheme {
        WriteScreen()
    }
}
