package com.hanhyo.commitlog.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hanhyo.commitlog.presentation.navigation.CommitLogNavHost

@Composable
fun CommitLogApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {},
        bottomBar = {},
        floatingActionButton = {},
    ) { innerPadding ->
        CommitLogNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
