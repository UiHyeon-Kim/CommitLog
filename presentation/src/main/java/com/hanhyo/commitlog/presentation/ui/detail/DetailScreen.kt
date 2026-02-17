package com.hanhyo.commitlog.presentation.ui.detail

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.common.extension.toRelativeString
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.AppBarNavItem
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = (uiState as? DetailUiState.Success)?.commit?.date?.toRelativeString() ?: "",
                navItem = AppBarNavItem.Back(onClick = onBack),
                actions = {
                    val commitId = (uiState as? DetailUiState.Success)?.commit?.id?.value
                    if (commitId != null) {
                        IconButton(onClick = { onNavigateToEdit(commitId) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "수정하기",
                                tint = CommitLogTheme.colors.textPrimary
                            )
                        }
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
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = CommitLogTheme.colors.primary
                    )
                }

                is DetailUiState.Error -> {
                    Text(
                        text = state.message,
                        color = CommitLogTheme.colors.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is DetailUiState.Success -> {
                    DetailContent(commit = state.commit)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContent(
    commit: Commit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.SpacingLarge, vertical = Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        // Title
        Text(
            text = commit.title.value,
            style = CommitLogTheme.typography.headlineLarge,
            color = CommitLogTheme.colors.textPrimary
        )

        // Tags
        if (commit.tags.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
            ) {
                commit.tags.forEach { tag ->
                    Text(
                        text = "#${tag.value}",
                        style = CommitLogTheme.typography.bodyMedium,
                        color = CommitLogTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingSmall))

        // Learned Content
        Section(title = "📝 오늘 배운 점") {
            Text(
                text = commit.learnedToday.value,
                style = CommitLogTheme.typography.bodyLarge,
                color = CommitLogTheme.colors.textSecondary
            )
        }

        // Analysis
        commit.analysis?.let { analysis ->
             Section(title = "🤖 AI 분석") {
                Text(
                    text = analysis.comment,
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textSecondary
                )
            }
        }

         // Difficulties
        commit.difficulties?.let { difficulties ->
             Section(title = "💦 어려웠던 점") {
                Text(
                    text = difficulties,
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textSecondary
                )
            }
        }

        // Tomorrow Plan
        commit.tomorrowPlan?.let { plan ->
             Section(title = "📅 내일의 계획") {
                Text(
                    text = plan,
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
    ) {
        Text(
            text = title,
            style = CommitLogTheme.typography.titleMedium,
            color = CommitLogTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}
