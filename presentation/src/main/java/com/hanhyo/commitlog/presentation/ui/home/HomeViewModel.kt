package com.hanhyo.commitlog.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel() {
    private val _effect = MutableSharedFlow<HomeEffect>(replay = 0)
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()


    fun navigateToWrite() {
        viewModelScope.launch {
            _effect.emit(HomeEffect.NavigateToWrite)
        }
    }

    fun navigateToDetail() {
        viewModelScope.launch {
            _effect.emit(HomeEffect.NavigateToDetail)
        }
    }
}

sealed interface HomeEffect {
    object NavigateToWrite : HomeEffect
    object NavigateToDetail : HomeEffect
}
