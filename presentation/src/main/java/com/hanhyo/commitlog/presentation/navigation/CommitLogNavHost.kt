package com.hanhyo.commitlog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hanhyo.commitlog.presentation.ui.detail.DetailScreen
import com.hanhyo.commitlog.presentation.ui.home.HomeScreen
import com.hanhyo.commitlog.presentation.ui.review.ReviewScreen
import com.hanhyo.commitlog.presentation.ui.splash.SplashScreen
import com.hanhyo.commitlog.presentation.ui.stats.StatisticsScreen
import com.hanhyo.commitlog.presentation.ui.write.WriteScreen

@Composable
fun CommitLogNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(HomeRoute)
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                onNavigateToWrite = {
                    navController.navigate(WriteRoute)
                },
                onNavigateToDetail = {
                    navController.navigate(DetailRoute)
                },
            )
        }

        composable<WriteRoute> {
            WriteScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<DetailRoute> {
            DetailScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<StatisticsRoute> {
            StatisticsScreen()
        }

        composable<ReviewRoute> {
            ReviewScreen()
        }
    }
}
