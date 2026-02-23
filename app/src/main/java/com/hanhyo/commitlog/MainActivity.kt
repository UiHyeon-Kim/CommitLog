package com.hanhyo.commitlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hanhyo.commitlog.presentation.navigation.CommitLogNavHost
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            CommitLogTheme {
                CommitLogNavHost()
            }
        }
    }
}
