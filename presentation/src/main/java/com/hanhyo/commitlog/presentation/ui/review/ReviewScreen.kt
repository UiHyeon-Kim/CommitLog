package com.hanhyo.commitlog.presentation.ui.review

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    ReviewContent()
}

@Composable
private fun ReviewContent(modifier: Modifier = Modifier) {

}

@Preview(showBackground = true)
@Composable
private fun ReviewScreenPreview() {
    CommitLogTheme {
        ReviewScreen()
    }
}
