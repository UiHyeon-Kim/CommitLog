package com.hanhyo.commitlog.presentation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hanhyo.commitlog.presentation.navigation.DetailRoute
import com.hanhyo.commitlog.presentation.navigation.MainRoute
import com.hanhyo.commitlog.presentation.navigation.SplashRoute
import com.hanhyo.commitlog.presentation.navigation.WriteRoute
import com.hanhyo.commitlog.presentation.navigation.homeDestination
import com.hanhyo.commitlog.presentation.ui.detail.DetailScreen
import com.hanhyo.commitlog.presentation.ui.main.MainScreen
import com.hanhyo.commitlog.presentation.ui.splash.SplashScreen
import com.hanhyo.commitlog.presentation.ui.write.WriteScreen

@Composable
fun CommitLogApp(
    navController: NavHostController = rememberNavController()
) {
    Surface {
        NavHost(
            navController = navController,
            startDestination = SplashRoute
        ) {
            composable<SplashRoute> {
                SplashScreen(
                    onFinished = {
                        navController.navigate(MainRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoute> {
                MainScreen(navController)
            }

            homeDestination(navController)
        }
    }
}
