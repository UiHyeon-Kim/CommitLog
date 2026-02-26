package com.hanhyo.commitlog.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.SearchQuery
import com.hanhyo.commitlog.domain.usecase.commit.SearchCommitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCommitsUseCase: SearchCommitsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    init {
        observeQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        _query
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { keyword ->
                searchCommits(keyword)
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun clearQuery() {
        _query.value = ""
    }

    fun search() {
        if (_query.value.isNotBlank()) {
            searchCommits(_query.value)
        }
    }

    private fun searchCommits(keyword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 검색어가 비어있는 경우(keyword.isBlank())에도 UseCase에서 전체 목록을 반환하도록 처리하거나,
            // 별도의 로직으로 전체 목록을 가져올 수 있습니다.
            // 여기서는 SearchCommitsUseCase가 빈 검색어에 대해 전체 목록을 반환한다고 가정하거나
            // (만약 아니라면) ObserveAllCommitsUseCase를 직접 활용하도록 확장할 수 있습니다.
            searchCommitsUseCase(SearchQuery(keyword = keyword))
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
}

data class SearchUiState(
    val searchResults: List<Commit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
