package com.hanhyo.commitlog.presentation.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.hanhyo.commitlog.presentation.ui.main.MainScreen
import com.hanhyo.commitlog.presentation.ui.search.SearchScreen

@Composable
fun CommitLogNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: Any = MainRoute
) {
    Surface {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<MainRoute> {
                MainScreen(
                    rootNavController = navController,
                    initialBottomRoute = null
                )
            }

            composable<ReviewRoute>(
                deepLinks = listOf(
                    navDeepLink { uriPattern = "app://commitlog/review" }
                )
            ) {
                MainScreen(
                    rootNavController = navController,
                    initialBottomRoute = ReviewRoute
                )
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
