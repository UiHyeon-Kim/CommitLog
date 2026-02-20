package com.hanhyo.commitlog.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.presentation.ui.home.components.EmptyState
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogHomeAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.card.CommitCard
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.home.components.CommitLogFloatingActionButton
import com.hanhyo.commitlog.presentation.ui.home.components.DateHeader
import com.hanhyo.commitlog.presentation.ui.home.components.SwipeToDeleteCard
import java.time.LocalDate

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToWrite: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val commits by viewModel.commits.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    HomeEffect.NavigateToWrite -> onNavigateToWrite()
                    is HomeEffect.NavigateToCommit -> onNavigateToDetail(effect.commitId)
                    is HomeEffect.ShowSnackbar -> {
                        val result = snackbarHostState.showSnackbar(
                            message = effect.message,
                            actionLabel = effect.actionLabel
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.undoDelete()
                        }
                    }
                }
            }
        }
    }

    HomeContent(
        commits = commits,
        totalCommitCount = uiState.totalCommitCount,
        isLoading = uiState.isLoading,
        snackbarHostState = snackbarHostState,
        onCommitClick = viewModel::navigateToCommit,
        onDelete = viewModel::deleteCommit,
        onNavigateToWrite = viewModel::navigateToWrite,
        onNavigateToSearch = onNavigateToSearch
    )
}

@Composable
private fun HomeContent(
    commits: List<Commit>,
    totalCommitCount: Int,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onCommitClick: (Long) -> Unit,
    onDelete: (Commit) -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CommitLogHomeAppBar(
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색",
                            tint = CommitLogTheme.colors.textTertiary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            CommitLogFloatingActionButton(
                onClick = onNavigateToWrite
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = CommitLogTheme.colors.background,
        modifier = modifier
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = CommitLogTheme.colors.primary
                    )
                }
            }

            // TODO: 스켈레톤뷰로 변경
            commits.isEmpty() -> {
                EmptyState(
                    emoji = "📝",
                    title = "첫 커밋을 작성해보세요!",
                    message = "오늘 배운 내용을 기록하고\nAI의 분석을 받아보세요",
                    modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                )
            }

            else -> {
                HomeList(
                    modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                    commits = commits,
                    totalCommitCount = totalCommitCount,
                    onCommitClick = onCommitClick,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun HomeList(
    commits: List<Commit>,
    totalCommitCount: Int,
    onCommitClick: (Long) -> Unit,
    onDelete: (Commit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupedCommits = remember(commits) {
        commits.groupBy { it.date }
            .toList()
            .sortedByDescending { it.first }
    }

    Box {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = Dimensions.SpacingLarge, vertical = Dimensions.SpacingMedium),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        Text(
            text = "총 ${totalCommitCount}개",
            style = CommitLogTheme.typography.bodyMedium,
            color = CommitLogTheme.colors.textTertiary,
            modifier = Modifier
                .padding(top = 100.dp, end = 16.dp)
                .background(
                    color = CommitLogTheme.colors.surface,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Preview(showBackground = true, name = "Home - With Commits")
@Composable
private fun HomeContentWithCommitsPreview() {
    val sampleCommits = listOf(
        Commit(
            id = CommitId(1L),
            date = LocalDate.now(),
            title = CommitTitle("First Commit"),
            learnedToday = LearnedContent("Learned something today"),
            tags = setOf(LearningTag("tag1")),
            createdAt = System.currentTimeMillis(),
            isDraft = false,
            difficulties = null,
            tomorrowPlan = null,
            analysis = null,
            analysisStatus = com.hanhyo.commitlog.domain.model.AnalysisStatus.NONE,
            updatedAt = null
        ),
        Commit(
            id = CommitId(2L),
            date = LocalDate.now(),
            title = CommitTitle("Second Commit"),
            learnedToday = LearnedContent("Learned something else today"),
            tags = setOf(LearningTag("tag2")),
            createdAt = System.currentTimeMillis(),
            isDraft = false,
            difficulties = null,
            tomorrowPlan = null,
            analysis = null,
            analysisStatus = com.hanhyo.commitlog.domain.model.AnalysisStatus.NONE,
            updatedAt = null
        ),
        Commit(
            id = CommitId(3L),
            title = CommitTitle("Third Commit"),
            date = LocalDate.now().minusDays(1),
            learnedToday = LearnedContent("Learned something else today"),
            tags = setOf(LearningTag("tag3")),
            createdAt = System.currentTimeMillis(),
            isDraft = false,
            difficulties = null,
            tomorrowPlan = null,
            analysis = null,
            analysisStatus = com.hanhyo.commitlog.domain.model.AnalysisStatus.NONE,
            updatedAt = null
        ),
    )
    CommitLogTheme {
        HomeContent(
            commits = sampleCommits,
            totalCommitCount = 10,
            isLoading = false,
            snackbarHostState = remember { SnackbarHostState() },
            onCommitClick = {},
            onDelete = {},
            onNavigateToWrite = {},
            onNavigateToSearch = {}
        )
    }
}

@Preview(showBackground = true, name = "Home - Loading")
@Composable
private fun HomeContentLoadingPreview() {
    CommitLogTheme {
        HomeContent(
            commits = emptyList(),
            totalCommitCount = 0,
            isLoading = true,
            snackbarHostState = remember { SnackbarHostState() },
            onCommitClick = {},
            onDelete = {},
            onNavigateToWrite = {},
            onNavigateToSearch = {}
        )
    }
}

@Preview(showBackground = true, name = "Home - Empty")
@Composable
private fun HomeContentEmptyPreview() {
    CommitLogTheme {
        HomeContent(
            commits = emptyList(),
            totalCommitCount = 0,
            isLoading = false,
            snackbarHostState = remember { SnackbarHostState() },
            onCommitClick = {},
            onDelete = {},
            onNavigateToWrite = {},
            onNavigateToSearch = {}
        )
    }
}
