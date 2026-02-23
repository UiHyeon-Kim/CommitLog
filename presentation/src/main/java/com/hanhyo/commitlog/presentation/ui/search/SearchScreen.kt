package com.hanhyo.commitlog.presentation.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanhyo.commitlog.domain.model.AnalysisStatus
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.search.components.SearchCommitItem
import com.hanhyo.commitlog.presentation.ui.search.components.SearchTopBar
import java.time.LocalDate

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()

    SearchContent(
        query = query,
        searchResults = uiState.searchResults,
        isLoading = uiState.isLoading,
        onQueryChange = viewModel::onQueryChanged,
        onSearch = viewModel::search,
        onBack = onBack,
        onClear = viewModel::clearQuery,
        onCommitClick = onNavigateToDetail
    )
}

@Composable
private fun SearchContent(
    query: String,
    searchResults: List<Commit>,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onCommitClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onBack = onBack,
                onClear = onClear
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
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(color = CommitLogTheme.colors.primary)
                }
            } else {
                if (searchResults.isEmpty() && query.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "검색 결과가 없습니다",
                            style = CommitLogTheme.typography.bodyLarge,
                            color = CommitLogTheme.colors.textSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(Dimensions.SpacingMedium),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
                    ) {
                        items(searchResults) { commit ->
                            SearchCommitItem(
                                commit = commit,
                                onClick = { onCommitClick(commit.id.value) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Search - Initial")
@Composable
private fun SearchContentInitialPreview() {
    CommitLogTheme {
        SearchContent(
            query = "",
            searchResults = emptyList(),
            isLoading = false,
            onQueryChange = {},
            onSearch = {},
            onBack = {},
            onClear = {},
            onCommitClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Search - With Results")
@Composable
private fun SearchContentWithResultsPreview() {
    val sampleCommits = listOf(
        Commit(
            CommitId(1),
            LocalDate.now(),
            CommitTitle("Jetpack Compose"),
            LearnedContent("State in Compose"),
            null,
            null,
            setOf(LearningTag("compose")),
            null,
            AnalysisStatus.NONE,
            false,
            0,
            null
        ),
        Commit(
            CommitId(2),
            LocalDate.now(),
            CommitTitle("Kotlin Coroutines"),
            LearnedContent("Launch and Async"),
            null,
            null,
            setOf(LearningTag("kotlin")),
            null,
            AnalysisStatus.NONE,
            false,
            0,
            null
        )
    )
    CommitLogTheme {
        SearchContent(
            query = "Compose",
            searchResults = sampleCommits,
            isLoading = false,
            onQueryChange = {},
            onSearch = {},
            onBack = {},
            onClear = {},
            onCommitClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Search - No Results")
@Composable
private fun SearchContentNoResultsPreview() {
    CommitLogTheme {
        SearchContent(
            query = "Invalid Query",
            searchResults = emptyList(),
            isLoading = false,
            onQueryChange = {},
            onSearch = {},
            onBack = {},
            onClear = {},
            onCommitClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Search - Loading")
@Composable
private fun SearchContentLoadingPreview() {
    CommitLogTheme {
        SearchContent(
            query = "Compose",
            searchResults = emptyList(),
            isLoading = true,
            onQueryChange = {},
            onSearch = {},
            onBack = {},
            onClear = {},
            onCommitClick = {}
        )
    }
}
