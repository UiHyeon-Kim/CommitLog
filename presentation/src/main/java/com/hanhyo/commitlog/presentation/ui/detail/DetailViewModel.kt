package com.hanhyo.commitlog.presentation.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.usecase.commit.DeleteCommitUseCase
import com.hanhyo.commitlog.domain.usecase.commit.GetCommitByIdUseCase
import com.hanhyo.commitlog.presentation.navigation.DetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCommitByIdUseCase: GetCommitByIdUseCase,
    private val deleteCommitUseCase: DeleteCommitUseCase,
) : ViewModel() {

    private val detailRoute: DetailRoute = savedStateHandle.toRoute()

    private val _effect = MutableSharedFlow<DetailEffect>(replay = 0)
    val effect: SharedFlow<DetailEffect> = _effect.asSharedFlow()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadCommit(detailRoute.commitId)
    }

    private fun loadCommit(commitId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getCommitByIdUseCase(CommitId(commitId))
                .onSuccess { commit ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            commit = commit
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun editCommit() {
        viewModelScope.launch {
            _uiState.value.commit?.let { commit ->
                _effect.emit(DetailEffect.NavigateToEdit(commit.id.value))
            }
        }
    }

    fun deleteCommit() {
        viewModelScope.launch {
            _uiState.value.commit?.let { commit ->
                deleteCommitUseCase(commit)
                    .onSuccess {
                        _effect.emit(DetailEffect.NavigateBack)
                    }
                    .onFailure {
                        _uiState.update { it.copy(error = "삭제 실패") }
                    }
            }
        }
    }
}

data class DetailUiState(
    val isLoading: Boolean = false,
    val commit: Commit? = null,
    val error: String? = null,
)

sealed interface DetailEffect {
    data object NavigateBack : DetailEffect
    data class NavigateToEdit(val commitId: Long) : DetailEffect
}
