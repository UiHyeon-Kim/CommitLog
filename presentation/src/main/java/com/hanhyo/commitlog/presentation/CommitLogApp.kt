package com.hanhyo.commitlog.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogBottomNavBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogHomeAppBar
import com.hanhyo.commitlog.presentation.designsystem.components.bar.model.BottomNavItem
import com.hanhyo.commitlog.presentation.navigation.CommitLogNavHost
import com.hanhyo.commitlog.presentation.navigation.HomeRoute

@Composable
fun CommitLogApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = BottomNavItem.entries.any { item ->
        currentDestination?.hasRoute(item.tabRouteClass) == true
    }

    Scaffold(
        topBar = {
            if (currentDestination == HomeRoute) {
                CommitLogHomeAppBar()
            }
        },
        bottomBar = {
            if (showBottomBar) {
                CommitLogBottomNavBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        },
        floatingActionButton = {},
    ) { innerPadding ->
        CommitLogNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
