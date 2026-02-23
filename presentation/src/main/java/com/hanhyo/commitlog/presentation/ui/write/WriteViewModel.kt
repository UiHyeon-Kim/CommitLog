package com.hanhyo.commitlog.presentation.ui.write

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.usecase.aianalysis.AnalyzeAndSaveCommitUseCase
import com.hanhyo.commitlog.domain.usecase.commit.DeleteAllDraftsUseCase
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
    private val getCommitByIdUseCase: GetCommitByIdUseCase,
    private val saveCommitUseCase: SaveCommitUseCase,
    private val updateCommitUseCase: UpdateCommitUseCase,
    private val analyzeAndSaveCommitUseCase: AnalyzeAndSaveCommitUseCase,
    private val deleteAllDraftsUseCase: DeleteAllDraftsUseCase,
) : ViewModel() {

    private val writeRoute: WriteRoute = savedStateHandle.toRoute()

    private val _effect = MutableSharedFlow<WriteEffect>(replay = 0)
    val effect: SharedFlow<WriteEffect> = _effect.asSharedFlow()

    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

    // 편집 모드일 때 ID 저장
    private var editingCommitId: Long? = null

    init {
        val commitId = writeRoute.commitId
        if (commitId != null) {
            loadCommit(commitId)
        }
    }

    private fun loadCommit(commitId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getCommitByIdUseCase(CommitId(commitId))
                .onSuccess { commit ->
                    editingCommitId = commit.id.value // ID 저장
                    val loadedTitle = if (commit.title.value == "제목 없음") "" else commit.title.value
                    val loadedLearnedToday = if (commit.learnedToday.value == "내용 없음") "" else commit.learnedToday.value
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isEditMode = true,
                            date = commit.date,
                            title = loadedTitle,
                            learnedToday = loadedLearnedToday,
                            difficulties = commit.difficulties ?: "",
                            tomorrowPlan = commit.tomorrowPlan ?: "",
                            canSave = validateInput(loadedTitle, loadedLearnedToday)
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.emit(WriteEffect.ShowError("커밋을 불러오지 못했습니다."))
                    _effect.emit(WriteEffect.NavigateBack)
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

            val commit = Commit.create(
                date = state.date,
                title = CommitTitle(state.title),
                learnedToday = LearnedContent(state.learnedToday),
                difficulties = state.difficulties.ifBlank { null },
                tomorrowPlan = state.tomorrowPlan.ifBlank { null },
                isDraft = false
            )

            if (state.isEditMode) {
                // 수정 모드
                val commitToUpdate = commit.copy(
                    id = CommitId(editingCommitId ?: 0L)
                )

                updateCommitUseCase(commitToUpdate)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.emit(WriteEffect.ShowSuccess("커밋이 수정되었습니다"))
                        _effect.emit(WriteEffect.NavigateBack)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.emit(
                            WriteEffect.ShowError(
                                error.message ?: "수정 실패"
                            )
                        )
                    }
            } else {
                // 새 커밋 저장 + AI 분석 예약
                analyzeAndSaveCommitUseCase(commit)
                    .onSuccess { savedId ->
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.emit(
                            WriteEffect.ShowSuccess(
                                "커밋이 저장되었습니다. AI가 분석을 시작합니다"
                            )
                        )
                        _effect.emit(WriteEffect.NavigateBack)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoading = false) }

                        val errorMessage = when {
                            error is IllegalArgumentException ->
                                error.message ?: "입력값이 올바르지 않습니다"
                            error.message?.contains("API") == true ->
                                "AI 분석을 시작할 수 없습니다. 잠시 후 다시 시도해주세요"
                            else ->
                                "저장 중 오류가 발생했습니다"
                        }

                        _effect.emit(WriteEffect.ShowError(errorMessage))
                    }
            }
        }
    }

    fun saveDraft() {
        val state = _uiState.value
        if (state.title.isBlank() || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val draft = Commit.create(
                date = state.date,
                title = CommitTitle(state.title),
                learnedToday = LearnedContent(state.learnedToday.ifBlank { "내용 없음" }),
                difficulties = state.difficulties.ifBlank { null },
                tomorrowPlan = state.tomorrowPlan.ifBlank { null },
                isDraft = true
            )

            // 기존 임시저장 내용들을 모두 지워서 최근 1개의 단일 초안(Draft)만 유지
            deleteAllDraftsUseCase()

            saveCommitUseCase(draft)
                .onSuccess {
                    _effect.emit(WriteEffect.ShowSuccess("임시저장 되었습니다"))
                    _effect.emit(WriteEffect.NavigateBack)
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.emit(WriteEffect.ShowError("임시저장 실패"))
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
