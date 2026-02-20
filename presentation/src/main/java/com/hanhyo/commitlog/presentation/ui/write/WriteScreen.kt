package com.hanhyo.commitlog.presentation.ui.write

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.presentation.common.extension.toKoreanFormat
import com.hanhyo.commitlog.presentation.designsystem.components.Button.CommitLogButton
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.AppBarNavItem
import com.hanhyo.commitlog.presentation.designsystem.components.indicator.InlineLoading
import com.hanhyo.commitlog.presentation.designsystem.components.textfield.CommitLogMultiLineTextField
import com.hanhyo.commitlog.presentation.designsystem.components.textfield.CommitLogTextField
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import java.time.LocalDate

@Composable
fun WriteScreen(
    viewModel: WriteViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Side Effect 관찰
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                WriteEffect.NavigateBack -> onBack()
                is WriteEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is WriteEffect.ShowSuccess -> {
                    // 성공 메시지는 이전 화면에서 보여주거나, 현재 화면에서 보여주고 딜레이 후 이동
                    // 여기서는 NavigateBack이 곧바로 호출되므로, 
                    // 호출한 쪽(Home)에서 결과를 받거나, 아니면 여기서 잠깐 보여주고 이동해야 함.
                    // 현재 로직상 NavigateBack도 같이 emit 되므로, 
                    // Home에서 메시지를 띄우는 게 좋지만, 일단 여기서도 처리.
                }
            }
        }
    }

    // 뒤로가기 핸들링
    androidx.activity.compose.BackHandler {
        if (!uiState.isEditMode && (uiState.title.isNotBlank() || uiState.learnedToday.isNotBlank())) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    if (showExitDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "작성 중인 내용이 있습니다", style = CommitLogTheme.typography.titleMedium) },
            text = { Text(text = "임시 저장하고 나가시겠습니까?", style = CommitLogTheme.typography.bodyMedium) },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.saveDraft()
                    }
                ) {
                    Text("임시 저장", color = CommitLogTheme.colors.primary)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showExitDialog = false
                        onBack()
                    }
                ) {
                    Text("나가기", color = CommitLogTheme.colors.textSecondary)
                }
            },
            containerColor = CommitLogTheme.colors.surface,
            titleContentColor = CommitLogTheme.colors.textPrimary,
            textContentColor = CommitLogTheme.colors.textSecondary
        )
    }

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = if (uiState.isEditMode) "커밋 수정" else "기록하기",
                navItem = AppBarNavItem.Back(onClick = {
                    if (!uiState.isEditMode && (uiState.title.isNotBlank() || uiState.learnedToday.isNotBlank())) {
                        showExitDialog = true
                    } else {
                        onBack()
                    }
                })
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CommitLogTheme.colors.background,
        modifier = Modifier.imePadding() // 키보드 올라왔을 때 스크롤 가능하도록 패딩 추가
    ) { padding ->
        WriteContent(
            date = uiState.date,
            title = uiState.title,
            learnedToday = uiState.learnedToday,
            difficulties = uiState.difficulties,
            tomorrowPlan = uiState.tomorrowPlan,
            isLoading = uiState.isLoading,
            isEditMode = uiState.isEditMode,
            canSave = uiState.canSave,
            onTitleChange = viewModel::updateTitle,
            onLearnedTodayChange = viewModel::updateLearnedToday,
            onDifficultiesChange = viewModel::updateDifficulties,
            onTomorrowPlanChange = viewModel::updateTomorrowPlan,
            onSave = viewModel::saveCommit,
            onSaveDraft = viewModel::saveDraft,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun WriteContent(
    date: LocalDate,
    title: String,
    learnedToday: String,
    difficulties: String,
    tomorrowPlan: String,
    isLoading: Boolean,
    isEditMode: Boolean,
    canSave: Boolean,
    onTitleChange: (String) -> Unit,
    onLearnedTodayChange: (String) -> Unit,
    onDifficultiesChange: (String) -> Unit,
    onTomorrowPlanChange: (String) -> Unit,
    onSave: () -> Unit,
    onSaveDraft: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.SpacingLarge, vertical = Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        // AI 분석 헤더
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = CommitLogTheme.colors.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(Dimensions.SpacingMedium),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
            ) {
                // AI Icon placeholder (using a simple Box with Icon for now or custom drawable if available)
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = CommitLogTheme.colors.primary.copy(alpha = 0.2f),
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CommitLogTheme.colors.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Column {
                    Text(
                        text = "AI 감정 분석",
                        style = CommitLogTheme.typography.titleSmall,
                        color = CommitLogTheme.colors.textPrimary
                    )
                    Text(
                        text = "AI가 당신의 기록에서 감정을 분석합니다",
                        style = CommitLogTheme.typography.bodySmall,
                        color = CommitLogTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingSmall))

        // 날짜 표시
        Text(
            text = date.toKoreanFormat(), // e.g., 2024년 5월 22일 확인 필요. toKoreanFormat이 포맷을 지원하는지 확인.
            style = CommitLogTheme.typography.bodyLarge,
            color = CommitLogTheme.colors.textSecondary
        )

        // 제목
        InputSection(title = "제목", required = true) {
            CommitLogTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = "제목을 입력하세요",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next)
            )
        }

        // 오늘 배운 점
        InputSection(title = "오늘 배운 점", required = true) {
            CommitLogMultiLineTextField(
                value = learnedToday,
                onValueChange = onLearnedTodayChange,
                placeholder = "오늘 새롭게 알게 된 사실은 무엇인가요?",
                enabled = !isLoading,
                minLines = 6,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next)
            )
        }

        // 어려웠던 점
        InputSection(title = "어려운 점", required = false) {
            CommitLogMultiLineTextField(
                value = difficulties,
                onValueChange = onDifficultiesChange,
                placeholder = "진행 중 마주친 장애물이나 고민이 있나요?",
                enabled = !isLoading,
                minLines = 4,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next)
            )
        }

        // 내일 할 일
        InputSection(title = "내일 할 일", required = false) {
            CommitLogMultiLineTextField(
                value = tomorrowPlan,
                onValueChange = onTomorrowPlanChange,
                placeholder = "내일은 어떤 작은 목표를 이룰까요?",
                enabled = !isLoading,
                minLines = 4,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { onSave() })
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

        // 버튼 (Loading indicator logic maintained)
        if (isLoading) {
            InlineLoading(
                message = "AI가 당신의 기록에서 감정을 분석합니다",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            CommitLogButton(
                text = if (isEditMode) "수정하기" else "저장하기",
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
    }
}

@Composable
private fun InputSection(
    title: String,
    required: Boolean,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(
                text = title,
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary
            )
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun WriteContentPreview() {
    CommitLogTheme {
        var title by remember { mutableStateOf("") }
        var learnedToday by remember { mutableStateOf("") }
        var difficulties by remember { mutableStateOf("") }
        var tomorrowPlan by remember { mutableStateOf("") }

        WriteContent(
            date = LocalDate.now(),
            title = title,
            learnedToday = learnedToday,
            difficulties = difficulties,
            tomorrowPlan = tomorrowPlan,
            isLoading = false,
            isEditMode = false,
            canSave = true,
            onTitleChange = { title = it },
            onLearnedTodayChange = { learnedToday = it },
            onDifficultiesChange = { difficulties = it },
            onTomorrowPlanChange = { tomorrowPlan = it },
            onSave = {},
            onSaveDraft = {}
        )
    }
}
