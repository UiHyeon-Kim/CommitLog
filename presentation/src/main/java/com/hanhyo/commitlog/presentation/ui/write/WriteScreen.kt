package com.hanhyo.commitlog.presentation.ui.write

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.presentation.common.extension.toKoreanFormat
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.AppBarNavItem
import com.hanhyo.commitlog.presentation.designsystem.components.button.CommitLogButton
import com.hanhyo.commitlog.presentation.designsystem.components.indicator.InlineLoading
import com.hanhyo.commitlog.presentation.designsystem.components.textfield.CommitLogMultiLineTextField
import com.hanhyo.commitlog.presentation.designsystem.components.textfield.CommitLogTextField
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.write.components.InputSection
import java.time.LocalDate

@Composable
fun WriteScreen(
    viewModel: WriteViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    WriteEffect.NavigateBack -> onBack()
                    is WriteEffect.ShowError -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }

                    is WriteEffect.ShowSuccess -> {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    val onSmartBack = {
        if (!uiState.isEditMode && (uiState.title.isNotBlank() || uiState.learnedToday.isNotBlank())) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    BackHandler {
        onSmartBack()
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "작성 중인 내용이 있습니다", style = CommitLogTheme.typography.titleMedium) },
            text = { Text(text = "임시 저장하고 나가시겠습니까?", style = CommitLogTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.saveDraft()
                    }
                ) {
                    Text("임시 저장", color = CommitLogTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(
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

    WriteContent(
        date = uiState.date,
        title = uiState.title,
        learnedToday = uiState.learnedToday,
        difficulties = uiState.difficulties,
        tomorrowPlan = uiState.tomorrowPlan,
        isLoading = uiState.isLoading,
        isEditMode = uiState.isEditMode,
        canSave = uiState.canSave,
        snackbarHostState = snackbarHostState,
        onTitleChange = viewModel::updateTitle,
        onLearnedTodayChange = viewModel::updateLearnedToday,
        onDifficultiesChange = viewModel::updateDifficulties,
        onTomorrowPlanChange = viewModel::updateTomorrowPlan,
        onSave = viewModel::saveCommit,
        onBack = onSmartBack
    )
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
    snackbarHostState: SnackbarHostState,
    onTitleChange: (String) -> Unit,
    onLearnedTodayChange: (String) -> Unit,
    onDifficultiesChange: (String) -> Unit,
    onTomorrowPlanChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = if (isEditMode) "커밋 수정" else "기록하기",
                navItem = AppBarNavItem.Back(onClick = onBack)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CommitLogTheme.colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = CommitLogTheme.colors.primary.copy(alpha = 0.2f),
                        modifier = Modifier
                            .height(40.dp)
                            .width(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CommitLogTheme.colors.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "AI 감정 분석",
                            style = CommitLogTheme.typography.titleMedium,
                            color = CommitLogTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AI가 당신의 기록에서 감정을 분석합니다",
                            style = CommitLogTheme.typography.bodySmall,
                            color = CommitLogTheme.colors.textSecondary
                        )
                    }
                }
            }

            Text(
                text = date.toKoreanFormat(),
                style = CommitLogTheme.typography.bodyLarge,
                color = CommitLogTheme.colors.textSecondary
            )

            InputSection(title = "제목") {
                CommitLogTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = "제목을 입력하세요",
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
            }

            InputSection(title = "오늘 배운 점") {
                CommitLogMultiLineTextField(
                    value = learnedToday,
                    onValueChange = onLearnedTodayChange,
                    placeholder = "오늘 새롭게 알게 된 사실은 무엇인가요?",
                    enabled = !isLoading,
                    minLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
            }

            InputSection(title = "어려운 점") {
                CommitLogMultiLineTextField(
                    value = difficulties,
                    onValueChange = onDifficultiesChange,
                    placeholder = "진행 중 마주친 장애물이나 고민이 있나요?",
                    enabled = !isLoading,
                    minLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
            }

            InputSection(title = "내일 할 일") {
                CommitLogMultiLineTextField(
                    value = tomorrowPlan,
                    onValueChange = onTomorrowPlanChange,
                    placeholder = "내일은 어떤 작은 목표를 이룰까요?",
                    enabled = !isLoading,
                    minLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

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
}

@Preview(showBackground = true, name = "Write - New")
@Composable
private fun WriteContentNewPreview() {
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
            onBack = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true, name = "Write - Edit")
@Composable
private fun WriteContentEditPreview() {
    CommitLogTheme {
        var title by remember { mutableStateOf("Jetpack Compose 마스터하기") }
        var learnedToday by remember { mutableStateOf("State와 Recomposition에 대해 깊이 이해했습니다.") }
        var difficulties by remember { mutableStateOf("초기에는 Side Effect 처리가 조금 헷갈렸습니다.") }
        var tomorrowPlan by remember { mutableStateOf("Custom Layout을 만들어볼 예정입니다.") }

        WriteContent(
            date = LocalDate.now(),
            title = title,
            learnedToday = learnedToday,
            difficulties = difficulties,
            tomorrowPlan = tomorrowPlan,
            isLoading = false,
            isEditMode = true,
            canSave = true,
            onTitleChange = { title = it },
            onLearnedTodayChange = { learnedToday = it },
            onDifficultiesChange = { difficulties = it },
            onTomorrowPlanChange = { tomorrowPlan = it },
            onSave = {},
            onBack = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true, name = "Write - Loading")
@Composable
private fun WriteContentLoadingPreview() {
    CommitLogTheme {
        WriteContent(
            date = LocalDate.now(),
            title = "로딩 중인 제목",
            learnedToday = "로딩 중인 내용",
            difficulties = "",
            tomorrowPlan = "",
            isLoading = true,
            isEditMode = false,
            canSave = false,
            onTitleChange = {},
            onLearnedTodayChange = {},
            onDifficultiesChange = {},
            onTomorrowPlanChange = {},
            onSave = {},
            onBack = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
