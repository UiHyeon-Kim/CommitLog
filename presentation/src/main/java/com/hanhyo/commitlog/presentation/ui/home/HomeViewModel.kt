package com.hanhyo.commitlog.presentation.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel() {
    private val _effect = MutableSharedFlow<HomeEffect>(replay = 0)
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()


    fun navigateToWrite() {
        _effect.tryEmit(HomeEffect.NavigateToWrite)
    }

    fun navigateToDetail() {
        _effect.tryEmit(HomeEffect.NavigateToDetail)
    }


}

sealed interface HomeEffect {
    object NavigateToWrite : HomeEffect
    object NavigateToDetail : HomeEffect
}
