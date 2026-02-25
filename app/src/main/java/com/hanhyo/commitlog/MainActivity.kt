package com.hanhyo.commitlog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hanhyo.commitlog.presentation.navigation.CommitLogNavHost
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.util.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationScheduler.scheduleMonthlyReviewNotification(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationScheduler.scheduleMonthlyReviewNotification(this)
            }
        } else {
            NotificationScheduler.scheduleMonthlyReviewNotification(this)
        }

        enableEdgeToEdge()
        setContent {
            CommitLogTheme {
                CommitLogNavHost()
            }
        }
    }
}
