package com.hanhyo.commitlog.presentation.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToWrite: () -> Unit,
    onNavigateToDetail: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    HomeEffect.NavigateToWrite -> onNavigateToWrite()
                    HomeEffect.NavigateToDetail -> onNavigateToDetail()
                }
            }
        }
    }

    HomeContent(
        onWriteClick = viewModel::navigateToWrite,
        onDetailClick = viewModel::navigateToDetail,
    )
}

@Composable
private fun HomeContent(
    onWriteClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(R.string.bottom_nav_home))

        Button(
            onClick = onWriteClick,
        ) {
            Text(text = "작성 화면")
        }

        Button(
            onClick = onDetailClick,
        ) {
            Text(text = "상세 화면")
        }
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    CommitLogTheme {
        HomeContent(
            onWriteClick = {},
            onDetailClick = {}
        )
    }
}
