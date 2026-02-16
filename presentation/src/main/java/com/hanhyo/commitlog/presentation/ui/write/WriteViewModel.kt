package com.hanhyo.commitlog.presentation.ui.write

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanhyo.commitlog.domain.common.Result
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.usecase.aianalysis.AnalyzeAndSaveCommitUseCase
import com.hanhyo.commitlog.domain.usecase.commit.GetCommitByIdUseCase
import com.hanhyo.commitlog.domain.usecase.commit.SaveCommitUseCase
import com.hanhyo.commitlog.domain.usecase.commit.UpdateCommitUseCase
import com.hanhyo.commitlog.presentation.navigation.WriteRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val analyzeAndSaveCommitUseCase: AnalyzeAndSaveCommitUseCase,
    private val getCommitByIdUseCase: GetCommitByIdUseCase,
    private val saveCommitUseCase: SaveCommitUseCase,
    private val updateCommitUseCase: UpdateCommitUseCase,
) : ViewModel() {

    private val writeRoute: WriteRoute = savedStateHandle.toRoute()

    private val _effect = MutableSharedFlow<WriteEffect>(replay = 0)
    val effect: SharedFlow<WriteEffect> = _effect.asSharedFlow()

    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

    init {
        val commitId = writeRoute.commitId
        if (commitId != null) {
            loadCommit(commitId)
        }
    }

    private fun loadCommit(commitId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getCommitByIdUseCase(CommitId(commitId))) {
                is Result.Success -> {
                    val commit = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isEditMode = true,
                            date = commit.date,
                            // 제목이 없으면 빈 문자열 (방어 코드)
                            title = commit.title.value,
                            learnedToday = commit.learnedToday.value,
                            difficulties = commit.difficulties ?: "",
                            tomorrowPlan = commit.tomorrowPlan ?: "",
                            canSave = true // 불러온 데이터는 기본적으로 저장 가능 상태
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.emit(WriteEffect.ShowError("커밋을 불러오지 못했습니다."))
                    _effect.emit(WriteEffect.NavigateBack)
                }

                is Result.Loading -> {}
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, canSave = validateInput(title, it.learnedToday)) }
    }

    fun updateLearnedToday(learnedToday: String) {
        _uiState.update { it.copy(learnedToday = learnedToday, canSave = validateInput(it.title, learnedToday)) }
    }

    fun updateDifficulties(difficulties: String) {
        _uiState.update { it.copy(difficulties = difficulties) }
    }

    fun updateTomorrowPlan(tomorrowPlan: String) {
        _uiState.update { it.copy(tomorrowPlan = tomorrowPlan) }
    }

    fun saveCommit() {
        val state = _uiState.value
        if (!state.canSave || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val commit = Commit.create(
                    date = state.date,
                    title = CommitTitle(state.title),
                    learnedToday = LearnedContent(state.learnedToday),
                    difficulties = state.difficulties.ifBlank { null },
                    tomorrowPlan = state.tomorrowPlan.ifBlank { null },
                    isDraft = false
                )

                // 사용자가 빠른 저장을 원함 -> 분석 없이 저장 후 종료
                // 추후 Worker 등을 통해 백그라운드 분석 적용 가능
                val result = if (state.isEditMode) {
                    updateCommitUseCase(commit)
                } else {
                    // analyzeAndSaveCommitUseCase(commit) -> 너무 오래 걸림
                    try {
                        saveCommitUseCase(commit)
                        Result.Success(commit)
                    } catch (e: Exception) {
                        Result.Error(com.hanhyo.commitlog.domain.common.DomainError.DatabaseError(e.message ?: "저장 실패", e))
                    }
                }

                when (result) {
                    is Result.Success -> {
                        _effect.emit(WriteEffect.ShowSuccess("커밋이 저장되었습니다"))
                        _effect.emit(WriteEffect.NavigateBack)
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.emit(WriteEffect.ShowError(result.error.message))
                    }
                    is Result.Loading -> {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _effect.emit(WriteEffect.ShowError(e.message ?: "유효하지 않은 입력"))
            }
        }
    }

    fun saveDraft() {
        val state = _uiState.value
        if (state.title.isBlank() || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val draft = Commit.create(
                    date = state.date,
                    title = CommitTitle(state.title),
                    learnedToday = LearnedContent(state.learnedToday.ifBlank { "내용 없음" }),
                    difficulties = state.difficulties.ifBlank { null },
                    tomorrowPlan = state.tomorrowPlan.ifBlank { null },
                    isDraft = true
                )

                when (saveCommitUseCase(draft)) {
                    is Result.Success -> {
                        _effect.emit(WriteEffect.ShowSuccess("임시저장 되었습니다"))
                        _effect.emit(WriteEffect.NavigateBack)
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.emit(WriteEffect.ShowError("임시저장 실패"))
                    }
                    is Result.Loading -> {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _effect.emit(WriteEffect.ShowError(e.message ?: "임시저장 실패"))
            }
        }
    }

    private fun validateInput(title: String, learnedToday: String): Boolean {
        return title.isNotBlank() && learnedToday.isNotBlank()
    }
}

data class WriteUiState(
    val date: LocalDate = LocalDate.now(),
    val title: String = "",
    val learnedToday: String = "",
    val difficulties: String = "",
    val tomorrowPlan: String = "",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val canSave: Boolean = false,
)

sealed interface WriteEffect {
    data object NavigateBack : WriteEffect
    data class ShowError(val message: String) : WriteEffect
    data class ShowSuccess(val message: String) : WriteEffect
}
