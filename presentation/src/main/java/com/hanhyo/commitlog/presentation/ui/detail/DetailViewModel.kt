package com.hanhyo.commitlog.presentation.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.usecase.commit.GetCommitByIdUseCase
import com.hanhyo.commitlog.presentation.navigation.DetailRoute
import com.hanhyo.commitlog.presentation.ui.detail.DetailUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCommitByIdUseCase: GetCommitByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        val commitId = savedStateHandle.toRoute<DetailRoute>().commitId
        loadCommit(commitId)
    }

    private fun loadCommit(id: Long) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            when (val result = getCommitByIdUseCase(CommitId(id))) {
                is Result.Success -> {
                    _uiState.value = Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = Error(result.error.message)
                }

                Result.Loading -> Unit
            }
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val commit: Commit) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
