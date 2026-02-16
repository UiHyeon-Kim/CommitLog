package com.hanhyo.commitlog.presentation.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogBottomNavBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.BottomNavItem
import com.hanhyo.commitlog.presentation.navigation.DetailRoute
import com.hanhyo.commitlog.presentation.navigation.HomeRoute
import com.hanhyo.commitlog.presentation.navigation.ReviewRoute
import com.hanhyo.commitlog.presentation.navigation.StatisticsRoute
import com.hanhyo.commitlog.presentation.navigation.WriteRoute
import com.hanhyo.commitlog.presentation.ui.home.HomeScreen
import com.hanhyo.commitlog.presentation.ui.review.ReviewScreen
import com.hanhyo.commitlog.presentation.ui.stats.StatisticsScreen

@SuppressLint("RestrictedApi")
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

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CommitLogBottomNavBar(
                    navController = bottomNavController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onNavigateToWrite = {
                        rootNavController.navigate(WriteRoute())
                    },
                    onNavigateToDetail = { commitId ->
                        rootNavController.navigate(DetailRoute(commitId))
                    }
                )
            }
            composable<StatisticsRoute> { StatisticsScreen() }
            composable<ReviewRoute> { ReviewScreen() }
        }
    }
}
