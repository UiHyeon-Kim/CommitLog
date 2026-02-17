package com.hanhyo.commitlog.presentation.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.presentation.designsystem.components.Button.CommitLogButton
import com.hanhyo.commitlog.presentation.designsystem.components.EmptyState
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.indicator.FullScreenLoading
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = "월간 회고",
            )
        },
        containerColor = CommitLogTheme.colors.background
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 월 선택 헤더
            MonthSelector(
                year = uiState.selectedYear,
                month = uiState.selectedMonth,
                onPrevious = viewModel::goToPreviousMonth,
                onNext = viewModel::goToNextMonth,
            )

            // 콘텐츠 영역
            when {
                uiState.isLoading -> {
                    FullScreenLoading(
                        message = "AI가 회고를 생성하고 있어요..."
                    )
                }

                uiState.review != null -> {
                    ReviewResult(
                        review = uiState.review!!,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = Dimensions.SpacingLarge),
                    )
                }

                uiState.errorMessage != null -> {
                    EmptyState(
                        emoji = "⚠️",
                        title = "회고 생성 실패",
                        message = uiState.errorMessage!!,
                        action = {
                            CommitLogButton(
                                text = "다시 시도",
                                onClick = viewModel::generateReview,
                            )
                        },
                    )
                }

                else -> {
                    EmptyState(
                        emoji = "📖",
                        title = "${uiState.selectedMonth}월 학습 회고",
                        message = "AI가 분석한 월간 학습 리포트를\n생성해보세요",
                        action = {
                            CommitLogButton(
                                text = "회고 생성하기",
                                onClick = viewModel::generateReview,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    year: Int,
    month: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.SpacingMedium, vertical = Dimensions.SpacingSmall),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "이전 달",
                tint = CommitLogTheme.colors.textPrimary,
            )
        }

        Text(
            text = "${year}년 ${month}월",
            style = CommitLogTheme.typography.titleMedium,
            color = CommitLogTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = Dimensions.SpacingLarge),
        )

        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "다음 달",
                tint = CommitLogTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
private fun ReviewResult(
    review: MonthlyReview,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium),
    ) {
        // 통계 요약 카드
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium),
        ) {
            SummaryStatCard(
                emoji = "📝",
                label = "총 커밋",
                value = "${review.totalCommitCount}개",
                modifier = Modifier.weight(1f),
            )

            val topMood = review.getMostFrequentMood()
            SummaryStatCard(
                emoji = topMood?.emoji ?: "😐",
                label = "대표 Mood",
                value = topMood?.displayNameKo ?: "-",
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium),
        ) {
            val topTag = review.getMostFrequentTag()
            SummaryStatCard(
                emoji = "🏷️",
                label = "주요 태그",
                value = topTag?.value ?: "-",
                modifier = Modifier.weight(1f),
            )
            SummaryStatCard(
                emoji = "📅",
                label = "활동 주차",
                value = "${review.weeklyCommitCount.size}주",
                modifier = Modifier.weight(1f),
            )
        }

        // AI 회고 카드
        ReviewCard(
            title = "AI 학습 회고",
            emoji = "🤖",
            content = review.aiSummary,
        )

        // Mood 분포 카드
        if (review.moodDistribution.isNotEmpty()) {
            ReviewCard(
                title = "Mood 분포",
                emoji = "📊",
                content = review.moodDistribution.entries
                    .sortedByDescending { it.value }
                    .joinToString("\n") { (mood, count) ->
                        "${mood.emoji} ${mood.displayNameKo}: ${count}회 (${review.getMoodPercentage(mood).toInt()}%)"
                    },
            )
        }

        // 태그 분포 카드
        if (review.tagDistribution.isNotEmpty()) {
            ReviewCard(
                title = "학습 태그",
                emoji = "🏷️",
                content = review.tagDistribution.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .joinToString("\n") { (tag, count) ->
                        "#${tag.value}: ${count}회 (${review.getTagPercentage(tag).toInt()}%)"
                    },
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
    }
}

@Composable
private fun SummaryStatCard(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.surface,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = emoji, fontSize = 14.sp)
                Text(
                    text = label,
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textSecondary,
                )
            }
            Text(
                text = value,
                style = CommitLogTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = CommitLogTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
private fun ReviewCard(
    title: String,
    emoji: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.surface,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = emoji, fontSize = 18.sp)
                Text(
                    text = title,
                    style = CommitLogTheme.typography.titleMedium,
                    color = CommitLogTheme.colors.textPrimary,
                )
            }

            Text(
                text = content,
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary,
                lineHeight = 22.sp,
            )
        }
    }
}
