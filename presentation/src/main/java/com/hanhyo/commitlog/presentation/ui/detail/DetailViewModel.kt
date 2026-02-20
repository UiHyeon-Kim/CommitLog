package com.hanhyo.commitlog.presentation.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.usecase.commit.DeleteCommitUseCase
import com.hanhyo.commitlog.domain.usecase.commit.ObserveCommitUseCase
import com.hanhyo.commitlog.presentation.navigation.DetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeCommitUseCase: ObserveCommitUseCase,
    private val deleteCommitUseCase: DeleteCommitUseCase,
    private val scheduleAnalysisUseCase: com.hanhyo.commitlog.domain.usecase.aianalysis.ScheduleAnalysisUseCase,
) : ViewModel() {

    private val detailRoute: DetailRoute = savedStateHandle.toRoute()
    private val commitId = detailRoute.commitId

    private val _effect = MutableSharedFlow<DetailEffect>(replay = 0)
    val effect: SharedFlow<DetailEffect> = _effect.asSharedFlow()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadCommit()
    }

    private fun loadCommit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            observeCommitUseCase(CommitId(commitId))
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { commit ->
                    if (commit != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                commit = commit,
                                error = null
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "커밋을 찾을 수 없습니다.") }
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

    fun scheduleAnalysis() {
        viewModelScope.launch {
            _uiState.value.commit?.let { commit ->
                scheduleAnalysisUseCase(commit.id.value)
                    .onSuccess {
                        _effect.emit(DetailEffect.ShowSnackbar("AI 분석이 시작되었습니다."))
                    }
                    .onFailure {
                        _effect.emit(DetailEffect.ShowSnackbar("분석 요청 실패"))
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
    data class ShowSnackbar(val message: String) : DetailEffect
}
