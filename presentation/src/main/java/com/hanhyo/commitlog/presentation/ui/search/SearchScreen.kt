package com.hanhyo.commitlog.presentation.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.presentation.designsystem.components.card.CommitCard
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()

    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChanged = viewModel::onQueryChanged,
                onBack = onBack,
                onClear = { viewModel.onQueryChanged("") }
            )
        },
        containerColor = CommitLogTheme.colors.background
    ) { paddingValues ->
        SearchContent(
            modifier = Modifier.padding(paddingValues),
            searchResults = uiState.searchResults,
            isLoading = uiState.isLoading,
            onCommitClick = { onNavigateToDetail(it.value) }
        )
    }
}

@Composable
fun SearchTopBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.background(CommitLogTheme.colors.background)
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .height(Dimensions.TopAppBarHeight)
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = CommitLogTheme.colors.textPrimary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "커밋 검색...",
                        style = CommitLogTheme.typography.bodyLarge,
                        color = CommitLogTheme.colors.textTertiary
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    textStyle = CommitLogTheme.typography.bodyLarge.copy(
                        color = CommitLogTheme.colors.textPrimary
                    ),
                    cursorBrush = SolidColor(CommitLogTheme.colors.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }

            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "지우기",
                        tint = CommitLogTheme.colors.textTertiary
                    )
                }
            } else {
                 IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = CommitLogTheme.colors.textTertiary
                    )
                }
            }
        }
        HorizontalDivider(thickness = 0.3.dp, color = CommitLogTheme.colors.border)
    }
}

@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    searchResults: List<Commit>,
    isLoading: Boolean,
    onCommitClick: (com.hanhyo.commitlog.domain.model.CommitId) -> Unit
) {
    if (isLoading) {
        // Loading State (Optional: Add a spinner if needed, but for local search it might be fast)
        // For now, keeping it simple as per "Surgical Precision"
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimensions.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        items(
            items = searchResults,
            key = { it.id.value }
        ) { commit ->
            CommitCard(
                commit = commit,
                onClick = { onCommitClick(commit.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
             Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
        }
    }
}
