package com.hanhyo.commitlog.presentation.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.domain.model.AnalysisStatus
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitAnalysis
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.DifficultyLevel
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogHomeAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.card.CommitCard
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.home.components.CommitLogFloatingActionButton
import com.hanhyo.commitlog.presentation.ui.home.components.DateHeader
import com.hanhyo.commitlog.presentation.ui.home.components.EmptyState
import com.hanhyo.commitlog.presentation.ui.home.components.HomeSkeleton
import com.hanhyo.commitlog.presentation.ui.home.components.SwipeToDeleteCard
import java.time.LocalDate

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToWrite: (Long?) -> Unit,
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
                    is HomeEffect.NavigateToWrite -> onNavigateToWrite(effect.draftId)
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
        isLoading = uiState.isLoading,
        commits = commits,
        totalCommitCount = uiState.totalCommitCount,
        showDraftDialog = uiState.showDraftDialog,
        onCommitClick = viewModel::navigateToCommit,
        onDelete = viewModel::deleteCommit,
        onNavigateToSearch = onNavigateToSearch,
        onFabClick = viewModel::onFabClicked,
        onDraftDialogDismiss = viewModel::onDraftDialogDismiss,
        onDraftDialogConfirm = viewModel::onDraftDialogConfirm,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun HomeContent(
    commits: List<Commit>,
    totalCommitCount: Int,
    isLoading: Boolean,
    showDraftDialog: Boolean,
    snackbarHostState: SnackbarHostState,
    onFabClick: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onDraftDialogDismiss: () -> Unit,
    onDelete: (Commit) -> Unit,
    onCommitClick: (Long) -> Unit,
    onDraftDialogConfirm: (Boolean) -> Unit,
) {
    val isSnackbarVisible = snackbarHostState.currentSnackbarData != null
    val fabOffset by animateDpAsState(
        targetValue = if (isSnackbarVisible) (-68).dp else 0.dp,
        label = "fab_slide_animation"
    )

    if (showDraftDialog) {
        AlertDialog(
            onDismissRequest = onDraftDialogDismiss,
            title = { Text(stringResource(R.string.home_draft_dialog_title)) },
            text = { Text(stringResource(R.string.home_draft_dialog_message)) },
            confirmButton = {
                TextButton(onClick = { onDraftDialogConfirm(true) }) {
                    Text(stringResource(R.string.home_draft_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDraftDialogConfirm(false) }) {
                    Text(stringResource(R.string.home_draft_dialog_dismiss))
                }
            },
            containerColor = CommitLogTheme.colors.surface,
            titleContentColor = CommitLogTheme.colors.textPrimary,
            textContentColor = CommitLogTheme.colors.textSecondary
        )
    }

    Scaffold(
        topBar = {
            CommitLogHomeAppBar(
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.common_search),
                            tint = CommitLogTheme.colors.textTertiary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            CommitLogFloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier.offset(y = fabOffset)
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.offset(y = 60.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CommitLogTheme.colors.surface,
                    contentColor = CommitLogTheme.colors.textPrimary,
                    actionColor = CommitLogTheme.colors.primary
                )
            }
        },
        containerColor = CommitLogTheme.colors.background,
    ) { paddingValues ->
        when {
            isLoading -> {
                HomeSkeleton(modifier = Modifier.padding(paddingValues))
            }

            commits.isEmpty() -> {
                EmptyState(
                    emoji = "📝",
                    title = stringResource(R.string.home_empty_title),
                    message = stringResource(R.string.home_empty_message),
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }

            else -> {
                val groupedCommits = remember(commits) {
                    commits.groupBy { it.date }
                        .toList()
                        .sortedByDescending { it.first }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        horizontal = Dimensions.SpacingLarge,
                        vertical = Dimensions.SpacingMedium
                    ),
                ) {
                    groupedCommits.forEachIndexed { index, (date, dateCommits) ->
                        item(key = "header_$date") {
                            DateHeader(
                                date = date,
                                totalCommitCount = if (index == 0) totalCommitCount else null
                            )
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

                            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))
                        }

                        item {
                            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    CommitLogTheme {
        HomeContent(
            commits = listOf(
                Commit(
                    id = CommitId(1),
                    title = CommitTitle("오늘 배운 내용"),
                    learnedToday = LearnedContent("오늘 배운 내용은 Jetpack Compose입니다. Jetpack Compose는..."),
                    tags = setOf(LearningTag("Jetpack"), LearningTag("Compose")),
                    date = LocalDate.now(),
                    difficulties = "어려웠던 점",
                    tomorrowPlan = "내일 학습 계획",
                    analysis = CommitAnalysis(
                        mood = AIMood.CURIOUS,
                        moodScore = 80,
                        difficultyLevel = DifficultyLevel.NORMAL,
                        comment = "코멘트"
                    ),
                    analysisStatus = AnalysisStatus.COMPLETED,
                    isDraft = false,
                    createdAt = 0L,
                    updatedAt = null
                ),
                Commit(
                    id = CommitId(2),
                    title = CommitTitle("오늘 배운 내용 2"),
                    learnedToday = LearnedContent("오늘 배운 내용은 Hilt입니다. Hilt는..."),
                    tags = setOf(LearningTag("Hilt")),
                    date = LocalDate.now().minusDays(1),
                    difficulties = "어려웠던 점",
                    tomorrowPlan = "내일 학습 계획",
                    analysis = CommitAnalysis(
                        mood = AIMood.FOCUSED,
                        moodScore = 90,
                        difficultyLevel = DifficultyLevel.EASY,
                        comment = "코멘트"
                    ),
                    analysisStatus = AnalysisStatus.COMPLETED,
                    isDraft = false,
                    createdAt = 0L,
                    updatedAt = null
                ),
                Commit(
                    id = CommitId(3),
                    title = CommitTitle("오늘 배운 내용 3"),
                    learnedToday = LearnedContent("오늘 배운 내용은..."),
                    tags = emptySet(),
                    date = LocalDate.now().minusDays(2),
                    difficulties = "어려웠던 점",
                    tomorrowPlan = "내일 학습 계획",
                    analysis = null,
                    analysisStatus = AnalysisStatus.NONE,
                    isDraft = false,
                    createdAt = 0L,
                    updatedAt = null
                )
            ),
            totalCommitCount = 3,
            isLoading = false,
            showDraftDialog = false,
            onCommitClick = {},
            onDelete = {},
            onNavigateToSearch = {},
            onFabClick = {},
            onDraftDialogDismiss = {},
            onDraftDialogConfirm = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
