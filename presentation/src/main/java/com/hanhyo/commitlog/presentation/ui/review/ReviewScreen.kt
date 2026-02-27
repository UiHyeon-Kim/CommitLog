package com.hanhyo.commitlog.presentation.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.button.CommitLogButton
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.home.components.EmptyState
import com.hanhyo.commitlog.presentation.ui.review.components.ReviewAiReportCard
import com.hanhyo.commitlog.presentation.ui.review.components.ReviewAiReportSkeleton
import com.hanhyo.commitlog.presentation.ui.review.components.ReviewMonthTabs
import com.hanhyo.commitlog.presentation.ui.review.components.SummaryStatCard
import java.time.YearMonth

@Composable
fun ReviewScreen(
    onNavigateToWrite: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ReviewEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }
    }

    // 더블 클릭 방지 로직
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val onDebouncedGenerate = {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 1000L && !uiState.isLoading) {
            lastClickTime = currentTime
            viewModel.generateReview()
        }
    }

    ReviewContent(
        uiState = uiState,
        availableMonths = uiState.availableMonths,
        onMonthSelected = viewModel::updateMonth,
        onGenerateReview = onDebouncedGenerate,
        onNavigateToWrite = onNavigateToWrite,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
private fun ReviewContent(
    uiState: ReviewUiState,
    availableMonths: List<YearMonth>,
    onMonthSelected: (Int, Int) -> Unit,
    onGenerateReview: () -> Unit,
    onNavigateToWrite: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = stringResource(R.string.review_title),
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CommitLogTheme.colors.surface,
                    contentColor = CommitLogTheme.colors.textPrimary,
                    actionColor = CommitLogTheme.colors.primary
                )
            }
        },
        containerColor = CommitLogTheme.colors.background,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ReviewMonthTabs(
                availableMonths = availableMonths,
                selectedYear = uiState.selectedYear,
                selectedMonth = uiState.selectedMonth,
                onMonthSelected = onMonthSelected
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(Dimensions.SpacingLarge),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.review_report_title, uiState.selectedMonth),
                        style = CommitLogTheme.typography.titleLarge,
                        color = CommitLogTheme.colors.primary
                    )
                    Text(
                        text = stringResource(R.string.review_description),
                        style = CommitLogTheme.typography.bodyMedium,
                        color = CommitLogTheme.colors.textSecondary
                    )
                }

                val review = uiState.review
                val isLoading = uiState.isLoading

                // 상단 통계 카드 영역 (로딩 중이거나 데이터가 없는 경우를 고려)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
                ) {
                    SummaryStatCard(
                        emoji = "📝",
                        label = stringResource(R.string.review_stat_total_commits),
                        value = when {
                            review != null -> "${review.totalCommitCount}"
                            else -> "${uiState.monthCommitCount}"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        emoji = if (uiState.commitsNeededForAverage > 0) "⏳" else (review?.getMostFrequentMood()?.emoji
                            ?: "🙂"),
                        label = stringResource(R.string.review_stat_average_mood),
                        value = when {
                            uiState.commitsNeededForAverage > 0 -> stringResource(
                                R.string.review_mood_threshold_message,
                                uiState.commitsNeededForAverage
                            )

                            review != null -> review.getMostFrequentMood()?.displayNameKo ?: "-"
                            else -> "-"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // AI 리포트 카드 또는 스켈레톤 영역
                when {
                    isLoading -> {
                        ReviewAiReportSkeleton()
                    }

                    review != null -> {
                        ReviewAiReportCard(
                            title = "AI 분석 리포트",
                            emoji = "🤖",
                            content = review.aiSummary
                        )
                    }

                    uiState.errorMessage != null -> {
                        EmptyState(
                            emoji = "⚠️",
                            title = stringResource(R.string.review_error_failed_to_generate),
                            message = uiState.errorMessage,
                            action = {
                                CommitLogButton(
                                    text = stringResource(R.string.common_retry),
                                    onClick = onGenerateReview,
                                )
                            },
                        )
                    }

                    uiState.monthCommitCount == 0 -> {
                        EmptyState(
                            emoji = "🏜️",
                            title = stringResource(R.string.review_empty_title),
                            message = stringResource(R.string.review_empty_message, uiState.selectedMonth),
                            action = {
                                CommitLogButton(
                                    text = stringResource(R.string.review_button_go_to_write),
                                    onClick = onNavigateToWrite,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                        )
                    }
                }

                // 하단 버튼 영역 (로딩 중에도 표시)
                if (uiState.monthCommitCount > 0 && uiState.errorMessage == null) {
                    val buttonText = if (review == null) {
                        stringResource(R.string.review_button_generate_ai)
                    } else {
                        stringResource(R.string.review_button_regenerate_ai)
                    }
                    CommitLogButton(
                        text = buttonText,
                        onClick = onGenerateReview,
                        enabled = true, // 로딩 중에도 클릭 가능하도록 유지 (ViewModel에서 중복 처리 방지 필요)
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Review - Before Generation")
@Composable
private fun ReviewContentBeforeGenerationPreview() {
    CommitLogTheme {
        ReviewContent(
            uiState = ReviewUiState(selectedYear = 2024, selectedMonth = 5, monthCommitCount = 15),
            availableMonths = listOf(YearMonth.of(2024, 5)),
            onMonthSelected = { _, _ -> },
            onGenerateReview = {},
            onNavigateToWrite = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true, name = "Review - Loading")
@Composable
private fun ReviewContentLoadingPreview() {
    CommitLogTheme {
        ReviewContent(
            uiState = ReviewUiState(selectedYear = 2024, selectedMonth = 5, isLoading = true),
            availableMonths = listOf(YearMonth.of(2024, 5)),
            onMonthSelected = { _, _ -> },
            onGenerateReview = {},
            onNavigateToWrite = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true, name = "Review - Completed")
@Composable
private fun ReviewContentCompletedPreview() {
    CommitLogTheme {
        ReviewContent(
            uiState = ReviewUiState(
                selectedYear = 2024,
                selectedMonth = 5,
                review = MonthlyReview(
                    year = 2024,
                    month = 5,
                    totalCommitCount = 20,
                    weeklyCommitCount = mapOf(1 to 5, 2 to 4, 3 to 6, 4 to 5),
                    moodDistribution = mapOf(
                        AIMood.PRODUCTIVE to 10,
                        AIMood.FOCUSED to 5
                    ),
                    tagDistribution = mapOf(
                        LearningTag("android") to 8,
                        LearningTag("kotlin") to 12
                    ),
                    aiSummary = "이번 달에는 특히 Jetpack Compose 학습에 많은 시간을 쏟으셨네요. 꾸준한 노력이 돋보입니다!"
                )
            ),
            availableMonths = listOf(YearMonth.of(2024, 5)),
            onMonthSelected = { _, _ -> },
            onGenerateReview = {},
            onNavigateToWrite = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true, name = "Review - Failed")
@Composable
private fun ReviewContentFailedPreview() {
    CommitLogTheme {
        ReviewContent(
            uiState = ReviewUiState(
                selectedYear = 2024,
                selectedMonth = 5,
                errorMessage = "서버와 통신 중 오류가 발생했습니다."
            ),
            availableMonths = listOf(YearMonth.of(2024, 5)),
            onMonthSelected = { _, _ -> },
            onGenerateReview = {},
            onNavigateToWrite = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
