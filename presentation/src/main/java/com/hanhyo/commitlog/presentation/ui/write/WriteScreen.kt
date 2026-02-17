package com.hanhyo.commitlog.presentation.ui.write

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanhyo.commitlog.presentation.common.extension.toRelativeString
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.AppBarNavItem
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun WriteScreen(
    onBack: () -> Unit,
    viewModel: WriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is WriteEvent.SaveSuccess -> {
                    Toast.makeText(context, "커밋이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    onBack()
                }
                is WriteEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = uiState.date.toRelativeString(),
                navItem = AppBarNavItem.Back(onClick = onBack),
                actions = {
                    TextButton(
                        onClick = viewModel::saveCommit,
                        enabled = !uiState.isLoading && !uiState.isSaving
                    ) {
                        Text(
                            text = "저장",
                            color = if (!uiState.isLoading && !uiState.isSaving) 
                                    CommitLogTheme.colors.primary 
                                else 
                                    CommitLogTheme.colors.textTertiary
                        )
                    }
                }
            )
        },
        containerColor = CommitLogTheme.colors.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = CommitLogTheme.colors.primary
                )
            } else {
                WriteContent(
                    uiState = uiState,
                    onTitleChange = viewModel::onTitleChange,
                    onContentChange = viewModel::onContentChange,
                    onTagsChange = viewModel::onTagsChange,
                    onDifficultiesChange = viewModel::onDifficultiesChange,
                    onTomorrowPlanChange = viewModel::onTomorrowPlanChange
                )
            }
            
            if (uiState.isSaving) {
                 // Overlay loader
                 Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CommitLogTheme.colors.background.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CommitLogTheme.colors.primary)
                }
            }
        }
    }
}

@Composable
fun WriteContent(
    uiState: WriteUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onTagsChange: (String) -> Unit,
    onDifficultiesChange: (String) -> Unit,
    onTomorrowPlanChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.SpacingLarge, vertical = Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        // Title
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text("제목") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        // Tags
        OutlinedTextField(
            value = uiState.tags,
            onValueChange = onTagsChange,
            label = { Text("태그 (쉼표로 구분)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        // Learned Content
        OutlinedTextField(
            value = uiState.content,
            onValueChange = onContentChange,
            label = { Text("오늘 배운 점") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            minLines = 3
        )

        // Difficulties
        OutlinedTextField(
            value = uiState.difficulties,
            onValueChange = onDifficultiesChange,
            label = { Text("어려웠던 점") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            minLines = 2
        )

        // Tomorrow Plan
        OutlinedTextField(
            value = uiState.tomorrowPlan,
            onValueChange = onTomorrowPlanChange,
            label = { Text("내일의 계획") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            minLines = 2
        )
    }
}
