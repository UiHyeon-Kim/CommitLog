package com.hanhyo.commitlog.presentation.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.domain.model.AIMood
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
import com.hanhyo.commitlog.presentation.designsystem.components.card.EmotionAnalysisCard
import com.hanhyo.commitlog.presentation.designsystem.components.indicator.FullScreenLoading
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
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

    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    DetailEffect.NavigateBack -> onBack()
                    is DetailEffect.NavigateToEdit -> onNavigateToEdit(effect.commitId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = "", // Title moved to content
                navItem = AppBarNavItem.Back(onClick = onBack),
                actions = {
                    if (uiState.commit != null) {
                        IconButton(onClick = { onNavigateToEdit(uiState.commit!!.id.value) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "수정",
                                tint = CommitLogTheme.colors.textPrimary
                            )
                        }

                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "삭제",
                                tint = CommitLogTheme.colors.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
             // "회고 추가하기" button from image seems to be a primary action. 
             // If it's for adding a new commit, FAB is good. 
             // If it's for adding reflection to current commit, maybe different.
             // The image shows a large bottom button "회고 추가하기". 
             // Let's assume it's a bottom fixed button or FAB. 
             // For now, I will use the existing FAB or Button if applicable, 
             // but `DetailScreen` usually doesn't have a "Add" button unless it's "Add Reflection".
             // The user image shows "회고 추가하기" at the bottom. 
             // I'll add it as a button at the bottom of the content for now.
        },
        containerColor = CommitLogTheme.colors.background
    ) { padding ->
        when {
            uiState.isLoading -> {
                FullScreenLoading()
            }
            uiState.commit != null -> {
                DetailContent(
                    commit = uiState.commit!!,
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
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

    // ... (Dialog code remains same)
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
}

@Composable
private fun DetailContent(
    commit: Commit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.SpacingLarge, vertical = Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        // 1. 태그 (Top Left)
        if (commit.tags.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
            ) {
                commit.tags.take(3).forEach { tag ->
                     Surface(
                        shape = MaterialTheme.shapes.extraLarge, // Rounded pill shape
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

        // 2. 제목
        Text(
            text = commit.title.value,
            style = CommitLogTheme.typography.headlineMedium, // Large Title
            color = CommitLogTheme.colors.textPrimary
        )

        // 3. 날짜 및 시간
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
        ) {
             Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Schedule,
                contentDescription = null,
                tint = CommitLogTheme.colors.textTertiary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "${commit.date.toKoreanFormat()} • 오후 10:45 기록", // TODO: Real time if available
                style = CommitLogTheme.typography.bodySmall,
                color = CommitLogTheme.colors.textTertiary
            )
        }

        HorizontalDivider(
            color = CommitLogTheme.colors.border,
            modifier = Modifier.padding(vertical = Dimensions.SpacingSmall)
        )

        // 4. 섹션들 (오늘 배운 점, 어려운 점, 내일 할 일)
        // 색상 바: Blue, Orange, Green as per image
        ContentSectionWithColorBar(
            title = "오늘 배운 점",
            content = commit.learnedToday.value,
            barColor = CommitLogTheme.colors.primary // Blue-ish
        )

        commit.difficulties?.let {
            ContentSectionWithColorBar(
                title = "어려운 점",
                content = it,
                barColor = CommitLogTheme.colors.accentOrange // Use explicit color name
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

        // 5. AI 인사이트 (AI Analysis)
        // Header
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CommitLogTheme.colors.primary
            )
            Text(
                text = "AI 인사이트",
                style = CommitLogTheme.typography.titleLarge,
                color = CommitLogTheme.colors.textPrimary
            )
        }
        
        // Card
        commit.analysis?.let { analysis ->
            Surface(
                shape = MaterialTheme.shapes.large,
                color = CommitLogTheme.colors.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimensions.SpacingLarge),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
                ) {
                    // Gauges Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 1. 학습 확신도 (Fake gauge for now, just circular indicator)
                        CircularIndicatorItem(
                            score = 85, // Placeholder or derived from analysis
                            label = "학습 확신도",
                            color = CommitLogTheme.colors.primary
                        )
                        // 2. 난이도
                        CircularIndicatorItem(
                            score = 0, // Text based
                            text = analysis.difficultyLevel.emoji, // Or text
                            label = "난이도",
                            color = CommitLogTheme.colors.accentPurple
                        )
                        // 3. 감정
                         CircularIndicatorItem(
                            score = 0, // Text based
                            text = analysis.mood.emoji,
                            label = "감정",
                            color = CommitLogTheme.colors.accentPink
                        )
                    }
                    
                    // Comment Box
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
        }
        
        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
        
        // "회고 추가하기" Button
        com.hanhyo.commitlog.presentation.designsystem.components.Button.CommitLogButton(
            text = "회고 추가하기", // Placeholder action
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
    }
}

@Composable
private fun ContentSectionWithColorBar(
    title: String,
    content: String,
    barColor: androidx.compose.ui.graphics.Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)) {
        Text(
            text = title,
            style = CommitLogTheme.typography.titleMedium,
            color = barColor
        )
        
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Vertical Bar
            Surface(
                color = barColor.copy(alpha = 0.5f),
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(),
                shape = MaterialTheme.shapes.small
            ) {}
            
            Spacer(modifier = Modifier.width(Dimensions.SpacingMedium))
            
            Text(
                text = content,
                style = CommitLogTheme.typography.bodyLarge,
                color = CommitLogTheme.colors.textPrimary,
                modifier = Modifier.padding(vertical = Dimensions.SpacingXSmall)
            )
        }
    }
}

@Composable
private fun CircularIndicatorItem(
    score: Int,
    text: String? = null,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
    ) {
        Box(
            contentAlignment = androidx.compose.ui.Alignment.Center,
            modifier = Modifier.size(64.dp)
        ) {
             CircularProgressIndicator(
                progress = { if (text == null) score / 100f else 1f },
                modifier = Modifier.fillMaxSize(),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
            )
            Text(
                text = text ?: "$score%",
                style = CommitLogTheme.typography.titleSmall,
                color = CommitLogTheme.colors.textPrimary
            )
        }
        Text(
            text = label,
            style = CommitLogTheme.typography.labelSmall,
            color = CommitLogTheme.colors.textSecondary
        )
    }
}

@Composable
private fun ContentSection(
    title: String,
    content: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)) {
        Text(
            text = title,
            style = CommitLogTheme.typography.titleMedium,
            color = CommitLogTheme.colors.primary
        )

        Text(
            text = content,
            style = CommitLogTheme.typography.bodyLarge,
            color = CommitLogTheme.colors.textPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailContentPreview() {
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
            isDraft = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )

        DetailContent(commit = mockCommit)
    }
}
