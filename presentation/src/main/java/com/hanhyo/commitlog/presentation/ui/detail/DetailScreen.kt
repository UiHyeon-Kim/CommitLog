package com.hanhyo.commitlog.presentation.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.hanhyo.commitlog.presentation.common.extension.toKoreanFormat
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.AppBarNavItem
import com.hanhyo.commitlog.presentation.designsystem.components.indicator.FullScreenLoading
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.detail.components.CircularIndicatorItem
import com.hanhyo.commitlog.presentation.ui.detail.components.ContentSectionWithColorBar
import com.hanhyo.commitlog.presentation.ui.detail.components.EmptyAnalysisCard
import java.time.LocalDate

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    DetailEffect.NavigateBack -> onBack()
                    is DetailEffect.NavigateToEdit -> onNavigateToEdit(effect.commitId)
                    is DetailEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "커밋 삭제",
                    style = CommitLogTheme.typography.titleMedium,
                    color = CommitLogTheme.colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "이 커밋을 삭제하시겠습니까? 삭제된 기록은 복구할 수 없습니다.",
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCommit()
                        showDeleteDialog = false
                    }
                ) {
                    Text("삭제", color = CommitLogTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소", color = CommitLogTheme.colors.textPrimary)
                }
            },
            containerColor = CommitLogTheme.colors.surface,
            textContentColor = CommitLogTheme.colors.textSecondary,
            titleContentColor = CommitLogTheme.colors.textPrimary
        )
    }

    when {
        uiState.isLoading -> {
            FullScreenLoading()
        }

        uiState.commit != null -> {
            DetailContent(
                commit = uiState.commit!!,
                snackbarHostState = snackbarHostState,
                onBack = onBack,
                onEdit = { onNavigateToEdit(uiState.commit!!.id.value) },
                onDelete = { showDeleteDialog = true },
                onScheduleAnalysis = viewModel::scheduleAnalysis
            )
        }

        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = uiState.error!!,
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.error
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    commit: Commit,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onScheduleAnalysis: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = "",
                navItem = AppBarNavItem.Back(onClick = onBack),
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "수정",
                            tint = CommitLogTheme.colors.textPrimary
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "삭제",
                            tint = CommitLogTheme.colors.error
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CommitLogTheme.colors.background,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensions.SpacingLarge, vertical = Dimensions.SpacingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
        ) {
            if (commit.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
                ) {
                    commit.tags.take(3).forEach { tag ->
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = CommitLogTheme.colors.primary.copy(alpha = 0.2f),
                        ) {
                            Text(
                                text = tag.value,
                                style = CommitLogTheme.typography.labelSmall,
                                color = CommitLogTheme.colors.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = commit.title.value,
                style = CommitLogTheme.typography.headlineMedium,
                color = CommitLogTheme.colors.textPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = CommitLogTheme.colors.textTertiary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${commit.date.toKoreanFormat()} • 오후 10:45 기록",
                    style = CommitLogTheme.typography.bodySmall,
                    color = CommitLogTheme.colors.textTertiary
                )
            }

            HorizontalDivider(
                color = CommitLogTheme.colors.border,
                modifier = Modifier.padding(vertical = Dimensions.SpacingSmall)
            )

            ContentSectionWithColorBar(
                title = "오늘 배운 점",
                content = commit.learnedToday.value,
                barColor = CommitLogTheme.colors.primary
            )

            commit.difficulties?.let {
                ContentSectionWithColorBar(
                    title = "어려운 점",
                    content = it,
                    barColor = CommitLogTheme.colors.accentOrange
                )
            }

            commit.tomorrowPlan?.let {
                ContentSectionWithColorBar(
                    title = "내일 할 일",
                    content = it,
                    barColor = CommitLogTheme.colors.accentPurple
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CommitLogTheme.colors.primary
                )
                Text(
                    text = "AI 인사이트",
                    style = CommitLogTheme.typography.titleLarge,
                    color = CommitLogTheme.colors.textPrimary
                )
            }

            if (commit.analysis != null) {
                val analysis = commit.analysis!!
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = CommitLogTheme.colors.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimensions.SpacingLarge),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CircularIndicatorItem(
                                score = 85,
                                label = "학습 확신도",
                                color = CommitLogTheme.colors.primary
                            )
                            CircularIndicatorItem(
                                score = 0,
                                text = analysis.difficultyLevel.emoji,
                                label = "난이도",
                                color = CommitLogTheme.colors.accentPurple
                            )
                            CircularIndicatorItem(
                                score = 0,
                                text = analysis.mood.emoji,
                                label = "감정",
                                color = CommitLogTheme.colors.accentPink
                            )
                        }

                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = CommitLogTheme.colors.background.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(Dimensions.SpacingMedium),
                                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
                            ) {
                                Text(
                                    text = "🤖",
                                    style = CommitLogTheme.typography.titleMedium
                                )
                                Text(
                                    text = "\"${analysis.comment}\"",
                                    style = CommitLogTheme.typography.bodyMedium,
                                    color = CommitLogTheme.colors.textPrimary
                                )
                            }
                        }
                    }
                }
            } else {
                EmptyAnalysisCard(
                    status = commit.analysisStatus,
                    onRegenerate = onScheduleAnalysis
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
        }
    }
}

@Preview(showBackground = true, name = "Detail - Completed")
@Composable
private fun DetailContentCompletedPreview() {
    CommitLogTheme {
        val mockCommit = Commit(
            id = CommitId(1),
            date = LocalDate.now(),
            title = CommitTitle("Jetpack Compose State 학습"),
            learnedToday = LearnedContent("remember와 mutableStateOf의 차이점을 공부했습니다."),
            difficulties = "State Hoisting 개념이 어려웠습니다.",
            tomorrowPlan = "ViewModel과 State 연동 학습",
            tags = setOf(LearningTag("compose"), LearningTag("kotlin")),
            analysis = CommitAnalysis(
                mood = AIMood.FOCUSED,
                moodScore = 85,
                difficultyLevel = DifficultyLevel.NORMAL,
                comment = "집중해서 학습하셨네요!"
            ),
            analysisStatus = AnalysisStatus.COMPLETED,
            isDraft = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )

        DetailContent(
            commit = mockCommit,
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onEdit = {},
            onDelete = {},
            onScheduleAnalysis = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail - Pending")
@Composable
private fun DetailContentPendingPreview() {
    CommitLogTheme {
        val mockCommit = Commit(
            id = CommitId(1),
            date = LocalDate.now(),
            title = CommitTitle("AI 분석 대기 중"),
            learnedToday = LearnedContent("분석이 완료되면 AI 인사이트를 확인할 수 있습니다."),
            difficulties = null,
            tomorrowPlan = null,
            tags = setOf(LearningTag("ai")),
            analysis = null,
            analysisStatus = AnalysisStatus.PENDING,
            isDraft = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )

        DetailContent(
            commit = mockCommit,
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onEdit = {},
            onDelete = {},
            onScheduleAnalysis = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail - Failed")
@Composable
private fun DetailContentFailedPreview() {
    CommitLogTheme {
        val mockCommit = Commit(
            id = CommitId(1),
            date = LocalDate.now(),
            title = CommitTitle("AI 분석 실패"),
            learnedToday = LearnedContent("분석 중 오류가 발생했습니다. 다시 시도해주세요."),
            difficulties = null,
            tomorrowPlan = null,
            tags = setOf(LearningTag("error")),
            analysis = null,
            analysisStatus = AnalysisStatus.FAILED,
            isDraft = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )

        DetailContent(
            commit = mockCommit,
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onEdit = {},
            onDelete = {},
            onScheduleAnalysis = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail - Minimal")
@Composable
private fun DetailContentMinimalPreview() {
    CommitLogTheme {
        val mockCommit = Commit(
            id = CommitId(1),
            date = LocalDate.now(),
            title = CommitTitle("간단한 기록"),
            learnedToday = LearnedContent("오늘 배운 내용만 간단하게 기록했습니다."),
            difficulties = null,
            tomorrowPlan = null,
            tags = emptySet(),
            analysis = null,
            analysisStatus = AnalysisStatus.COMPLETED,
            isDraft = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )

        DetailContent(
            commit = mockCommit,
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onEdit = {},
            onDelete = {},
            onScheduleAnalysis = {}
        )
    }
}
