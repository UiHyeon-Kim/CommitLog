package com.hanhyo.commitlog.presentation.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.presentation.common.extension.toRelativeString
import com.hanhyo.commitlog.presentation.designsystem.components.EmptyState
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogHomeAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.card.CommitCard
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.home.components.CommitLogFloatingActionButton
import com.hanhyo.commitlog.presentation.ui.home.components.SwipeToDeleteCard
import java.time.LocalDate

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToWrite: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val commits by viewModel.commits.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    HomeEffect.NavigateToWrite -> onNavigateToWrite()
                    is HomeEffect.NavigateToCommit -> onNavigateToDetail(effect.commitId)
                }
            }
        }
    }

    Scaffold(
        topBar = { CommitLogHomeAppBar() },
        floatingActionButton = {
            CommitLogFloatingActionButton(
                onClick = viewModel::navigateToWrite
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            commits.isEmpty() -> {
                EmptyState(
                    emoji = "📝",
                    title = "첫 커밋을 작성해보세요!",
                    message = "오늘 배운 내용을 기록하고\nAI의 분석을 받아보세요",
                    modifier = Modifier.padding(paddingValues)
                )
            }

            else -> {
                HomeContent(
                    modifier = Modifier.padding(paddingValues),
                    commits = commits,
                    onCommitClick = viewModel::navigateToCommit,
                    onDelete = viewModel::deleteCommit
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    commits: List<Commit>,
    onCommitClick: (Long) -> Unit,
    onDelete: (Commit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupedCommits = remember(commits) {
        commits.groupBy { it.date }
            .toList()
            .sortedByDescending { it.first }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimensions.SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        groupedCommits.forEach { (date, dateCommits) ->
            item(key = "header_$date") {
                DateHeader(date = date)
            }

            items(
                items = dateCommits,
                key = { it.id.value }
            ) { commit ->
                SwipeToDeleteCard(
                    onDelete = { onDelete(commit) }
                ) {
                    CommitCard(
                        commit = commit,
                        onClick = { onCommitClick(commit.id.value) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    Text(
        text = date.toRelativeString(),
        style = CommitLogTheme.typography.titleSmall,
        color = CommitLogTheme.colors.textTertiary,
        modifier = Modifier.padding(vertical = Dimensions.SpacingSmall)
    )
}

@Preview
@Composable
private fun HomeContentPreview() {
    CommitLogTheme {
        HomeContent(
            commits = emptyList(),
            onCommitClick = {},
            onDelete = {}
        )
    }
}
