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
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.presentation.R
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
        if (
            !uiState.isEditMode && (
                    uiState.title.isNotBlank() ||
                            uiState.learnedToday.isNotBlank() ||
                            uiState.difficulties.isNotBlank() ||
                            uiState.tomorrowPlan.isNotBlank()
                    )
        ) {
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
            title = {
                Text(
                    text = stringResource(R.string.write_dialog_exit_title),
                    style = CommitLogTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.write_dialog_exit_message),
                    style = CommitLogTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.saveDraft()
                    }
                ) {
                    Text(stringResource(R.string.write_dialog_confirm_save), color = CommitLogTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onBack()
                    }
                ) {
                    Text(stringResource(R.string.write_dialog_dismiss), color = CommitLogTheme.colors.textSecondary)
                }
            },
            containerColor = CommitLogTheme.colors.surface,
            titleContentColor = CommitLogTheme.colors.textPrimary,
            textContentColor = CommitLogTheme.colors.textSecondary
        )
    }

    // 더블 클릭 방지 로직
    var lastClickTime by remember { mutableStateOf(0L) }
    val onDebouncedSave = {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 1000L && !uiState.isLoading) {
            lastClickTime = currentTime
            viewModel.saveCommit()
        }
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
        onSave = onDebouncedSave,
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
                title = if (isEditMode) stringResource(R.string.write_title_edit) else stringResource(R.string.write_title_new),
                navItem = AppBarNavItem.Back(onClick = onBack)
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensions.SpacingLarge, vertical = Dimensions.SpacingMedium)
                .imePadding(),
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
                            text = stringResource(R.string.write_ai_analysis_title),
                            style = CommitLogTheme.typography.titleMedium,
                            color = CommitLogTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.write_ai_analysis_desc),
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

            InputSection(title = stringResource(R.string.write_section_title)) {
                CommitLogTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = stringResource(R.string.write_placeholder_title),
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
            }

            InputSection(title = stringResource(R.string.write_section_learned)) {
                CommitLogMultiLineTextField(
                    value = learnedToday,
                    onValueChange = onLearnedTodayChange,
                    placeholder = stringResource(R.string.write_placeholder_learned),
                    enabled = !isLoading,
                    minLines = 6,
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
            }

            InputSection(title = stringResource(R.string.write_section_difficulties)) {
                CommitLogMultiLineTextField(
                    value = difficulties,
                    onValueChange = onDifficultiesChange,
                    placeholder = stringResource(R.string.write_placeholder_difficulties),
                    enabled = !isLoading,
                    minLines = 4,
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
            }

            InputSection(title = stringResource(R.string.write_section_tomorrow)) {
                CommitLogMultiLineTextField(
                    value = tomorrowPlan,
                    onValueChange = onTomorrowPlanChange,
                    placeholder = stringResource(R.string.write_placeholder_tomorrow),
                    enabled = !isLoading,
                    minLines = 4,
                    modifier = Modifier
                        .fillMaxWidth(),
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
                    message = stringResource(R.string.write_loading_ai_analysis),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                CommitLogButton(
                    text = if (isEditMode) stringResource(R.string.write_button_edit) else stringResource(R.string.write_button_save),
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
