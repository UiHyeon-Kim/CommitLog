package com.hanhyo.commitlog.presentation.ui.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogBottomNavBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.BottomNavItem
import com.hanhyo.commitlog.presentation.navigation.DetailRoute
import com.hanhyo.commitlog.presentation.navigation.HomeRoute
import com.hanhyo.commitlog.presentation.navigation.ReviewRoute
import com.hanhyo.commitlog.presentation.navigation.SearchRoute
import com.hanhyo.commitlog.presentation.navigation.StatisticsRoute
import com.hanhyo.commitlog.presentation.navigation.WriteRoute
import com.hanhyo.commitlog.presentation.ui.home.HomeScreen
import com.hanhyo.commitlog.presentation.ui.review.ReviewScreen
import com.hanhyo.commitlog.presentation.ui.stats.StatisticsScreen

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = BottomNavItem.entries.any { item ->
        currentDestination?.hasRoute(item.tabRouteClass) == true
    }

    MainContent(
        rootNavController = rootNavController,
        bottomNavController = bottomNavController,
        currentDestination = currentDestination,
        showBottomBar = showBottomBar
    )
}

@Composable
private fun MainContent(
    rootNavController: NavHostController,
    bottomNavController: NavHostController,
    currentDestination: NavDestination?,
    showBottomBar: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CommitLogBottomNavBar(
                    navController = bottomNavController,
                    currentDestination = currentDestination
                )
            }
        },
        contentWindowInsets = WindowInsets.navigationBars,
        modifier = modifier
    ) { outerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = HomeRoute,
            modifier = Modifier
                .padding(outerPadding)
                .consumeWindowInsets(outerPadding)
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onNavigateToWrite = { draftId ->
                        rootNavController.navigate(WriteRoute(draftId))
                    },
                    onNavigateToDetail = { commitId ->
                        rootNavController.navigate(DetailRoute(commitId))
                    },
                    onNavigateToSearch = {
                        rootNavController.navigate(SearchRoute)
                    }
                )
            }
            composable<StatisticsRoute> {
                StatisticsScreen(
                    onNavigateToWrite = {
                        rootNavController.navigate(WriteRoute())
                    }
                )
            }
            composable<ReviewRoute>(
                deepLinks = listOf(
                    navDeepLink<ReviewRoute>(basePath = "app://commitlog/review")
                )
            ) { ReviewScreen() }
        }
    }
}
