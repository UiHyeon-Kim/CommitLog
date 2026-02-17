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
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.domain.usecase.aianalysis.AnalyzeAndSaveCommitUseCase
import com.hanhyo.commitlog.domain.usecase.commit.GetCommitByIdUseCase
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
    private val analyzeAndSaveCommitUseCase: AnalyzeAndSaveCommitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<WriteEvent>()
    val event: SharedFlow<WriteEvent> = _event.asSharedFlow()

    private var currentCommitId: CommitId? = null

    init {
        val commitId = savedStateHandle.toRoute<WriteRoute>().commitId
        if (commitId != null) {
            loadCommit(commitId)
        }
    }

    private fun loadCommit(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getCommitByIdUseCase(CommitId(id))
            when (result) {
                is Result.Success -> {
                    val commit = result.data
                    currentCommitId = commit.id
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            date = commit.date,
                            title = commit.title.value,
                            content = commit.learnedToday.value,
                            tags = commit.tags.joinToString(", ") { tag -> tag.value },
                            difficulties = commit.difficulties ?: "",
                            tomorrowPlan = commit.tomorrowPlan ?: ""
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _event.emit(WriteEvent.ShowError("커밋을 불러오는데 실패했습니다."))
                }

                Result.Loading -> Unit
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onContentChange(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun onTagsChange(newTags: String) {
        _uiState.update { it.copy(tags = newTags) }
    }

    fun onDifficultiesChange(newDifficulties: String) {
        _uiState.update { it.copy(difficulties = newDifficulties) }
    }

    fun onTomorrowPlanChange(newPlan: String) {
        _uiState.update { it.copy(tomorrowPlan = newPlan) }
    }

    fun saveCommit() {
        if (_uiState.value.title.isBlank() || _uiState.value.content.isBlank()) {
            viewModelScope.launch { _event.emit(WriteEvent.ShowError("제목과 내용을 입력해주세요.")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val commit = Commit(
                id = currentCommitId ?: CommitId(0), // 0 implies new commit usually, or handled by usecase
                date = _uiState.value.date,
                title = CommitTitle(_uiState.value.title),
                learnedToday = LearnedContent(_uiState.value.content),
                tags = _uiState.value.tags.split(",").map { LearningTag(it.trim()) }.filter { it.value.isNotEmpty() }
                    .toSet(),
                difficulties = _uiState.value.difficulties.ifBlank { null },
                tomorrowPlan = _uiState.value.tomorrowPlan.ifBlank { null },
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isDraft = false, // Assuming immediate publish for now
                analysis = null // Analysis will be handled by usecase
            )


            val result = analyzeAndSaveCommitUseCase(commit)

            _uiState.update { it.copy(isSaving = false) }

            when (result) {
                is Result.Success -> {
                    _event.emit(WriteEvent.SaveSuccess)
                }

                is Result.Error -> {
                    _event.emit(WriteEvent.ShowError(result.error.message))
                }

                is Result.Loading -> Unit
            }
        }
    }
}

data class WriteUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val date: LocalDate = LocalDate.now(),
    val title: String = "",
    val content: String = "",
    val tags: String = "",
    val difficulties: String = "",
    val tomorrowPlan: String = ""
)

sealed interface WriteEvent {
    data object SaveSuccess : WriteEvent
    data class ShowError(val message: String) : WriteEvent
}
