package com.hanhyo.commitlog.presentation.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.domain.model.MonthlyReview
import java.time.YearMonth
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.button.CommitLogButton
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.home.components.EmptyState
import com.hanhyo.commitlog.presentation.ui.review.components.ReviewAiReportCard
import com.hanhyo.commitlog.presentation.ui.review.components.ReviewAiReportSkeleton
import com.hanhyo.commitlog.presentation.ui.review.components.ReviewMonthTabs
import com.hanhyo.commitlog.presentation.ui.review.components.ReviewSkeleton
import com.hanhyo.commitlog.presentation.ui.review.components.SummaryStatCard

@Composable
fun ReviewScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToWrite: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    ReviewContent(
        uiState = uiState,
        availableMonths = uiState.availableMonths,
        onMonthSelected = viewModel::updateMonth,
        onGenerateReview = viewModel::generateReview,
        onNavigateToHome = onNavigateToHome,
        onNavigateToWrite = onNavigateToWrite,
        modifier = modifier
    )
}

@Composable
private fun ReviewContent(
    uiState: ReviewUiState,
    availableMonths: List<YearMonth>,
    onMonthSelected: (Int, Int) -> Unit,
    onGenerateReview: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToWrite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = "월간 회고",
            )
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
                        text = "${uiState.selectedMonth}월 회고 리포트",
                        style = CommitLogTheme.typography.titleLarge,
                        color = CommitLogTheme.colors.primary
                    )
                    Text(
                        text = "이번 달의 감정과 성장 기록을 확인하세요.",
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
                        label = "총 기록 수",
                        value = when {
                            review != null -> "${review.totalCommitCount}"
                            else -> "${uiState.monthCommitCount}"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        emoji = review?.getMostFrequentMood()?.emoji ?: "🙂",
                        label = "평균 감정",
                        value = review?.getMostFrequentMood()?.displayNameKo ?: "-",
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
                            title = "회고 생성 실패",
                            message = uiState.errorMessage,
                            action = {
                                CommitLogButton(
                                    text = "다시 시도",
                                    onClick = onGenerateReview,
                                )
                            },
                        )
                    }
                    uiState.monthCommitCount == 0 -> {
                        EmptyState(
                            emoji = "🏜️",
                            title = "기록이 아직 없어요",
                            message = "${uiState.selectedMonth}월에는 아직 기록된 커밋이 없습니다. 기록을 시작해볼까요?",
                            action = {
                                CommitLogButton(
                                    text = "첫 기록 남기러 가기",
                                    onClick = onNavigateToWrite,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                        )
                    }
                }

                // 하단 버튼 영역 (로딩 중에도 표시)
                if (uiState.monthCommitCount > 0 && uiState.errorMessage == null) {
                    val buttonText = if (review == null) "✨ AI 회고 생성하기" else "🔄 리뷰 다시 생성하기"
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
            onNavigateToHome = {},
            onNavigateToWrite = {}
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
            onNavigateToHome = {},
            onNavigateToWrite = {}
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
            onNavigateToHome = {},
            onNavigateToWrite = {}
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
            onNavigateToHome = {},
            onNavigateToWrite = {}
        )
    }
}
