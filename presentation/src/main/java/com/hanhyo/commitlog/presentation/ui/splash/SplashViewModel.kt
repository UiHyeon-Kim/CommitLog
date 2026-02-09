package com.hanhyo.commitlog.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    private val _effect = MutableSharedFlow<SplashEffect>(replay = 0)
    val effect: SharedFlow<SplashEffect> = _effect.asSharedFlow()


    init {
        initialCommitLog()
    }

    private fun initialCommitLog() {
        viewModelScope.launch {
            delay(1500)
            _effect.emit(SplashEffect.NavigateToHome)
        }
    }
}

sealed interface SplashEffect {
    object NavigateToHome : SplashEffect
}
