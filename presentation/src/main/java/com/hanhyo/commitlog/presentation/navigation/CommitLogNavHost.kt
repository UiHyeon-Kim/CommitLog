package com.hanhyo.commitlog.presentation.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hanhyo.commitlog.presentation.ui.main.MainScreen
import com.hanhyo.commitlog.presentation.ui.splash.SplashScreen
import com.hanhyo.commitlog.presentation.ui.search.SearchScreen

@Composable
fun CommitLogNavHost(
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

            composable<SearchRoute> {
                SearchScreen(
                    onNavigateToDetail = { commitId ->
                        navController.navigate(DetailRoute(commitId))
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
