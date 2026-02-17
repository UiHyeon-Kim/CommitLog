package com.hanhyo.commitlog.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.Streak
import com.hanhyo.commitlog.domain.usecase.commit.DeleteCommitUseCase
import com.hanhyo.commitlog.domain.usecase.commit.GetStreakUseCase
import com.hanhyo.commitlog.domain.usecase.commit.ObserveAllCommitsUseCase
import com.hanhyo.commitlog.domain.usecase.commit.SaveCommitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeAllCommitsUseCase: ObserveAllCommitsUseCase,
    private val deleteCommitUseCase: DeleteCommitUseCase,
    private val getStreakUseCase: GetStreakUseCase,
    private val saveCommitUseCase: SaveCommitUseCase,
    private val commitRepository: com.hanhyo.commitlog.domain.repository.CommitRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>(replay = 0)
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()

    private var recentlyDeletedCommit: Commit? = null

    val commits: StateFlow<List<Commit>> = observeAllCommitsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    init {
        loadStreak()
        loadTotalCommitCount()
    }

    private fun loadStreak() {
        viewModelScope.launch {
            when (val result = getStreakUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(streak = result.data) }
                }

                is Result.Error -> {}

                is Result.Loading -> {}
            }
        }
    }

    private fun loadTotalCommitCount() {
        viewModelScope.launch {
            commitRepository.observeTotalCommitCount()
                .collect { count ->
                    _uiState.update { it.copy(totalCommitCount = count) }
                }
        }
    }

    fun navigateToWrite() {
        viewModelScope.launch {
            _effect.emit(HomeEffect.NavigateToWrite)
        }
    }

    fun navigateToCommit(commitId: Long) {
        viewModelScope.launch {
            _effect.emit(HomeEffect.NavigateToCommit(commitId))
        }
    }

    fun deleteCommit(commit: Commit) {
        viewModelScope.launch {
            deleteCommitUseCase(commit)
            recentlyDeletedCommit = commit
            _effect.emit(HomeEffect.ShowSnackbar("커밋이 삭제되었습니다.", "실행 취소"))
        }
    }

    fun undoDelete() {
        val commitToRestore = recentlyDeletedCommit ?: return
        viewModelScope.launch {
            saveCommitUseCase(commitToRestore)
            recentlyDeletedCommit = null
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val streak: Streak? = null,
    val totalCommitCount: Int = 0,
    val error: String? = null,
)

sealed interface HomeEffect {
    data object NavigateToWrite : HomeEffect
    data class NavigateToCommit(val commitId: Long) : HomeEffect
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : HomeEffect
}
