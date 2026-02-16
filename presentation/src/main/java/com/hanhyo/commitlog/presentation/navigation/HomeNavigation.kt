package com.hanhyo.commitlog.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.hanhyo.commitlog.presentation.ui.detail.DetailScreen
import com.hanhyo.commitlog.presentation.ui.write.WriteScreen

fun NavGraphBuilder.homeDestination(navController: NavHostController) {

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
            },
            onNavigateToEdit = { commitId ->
                navController.navigate(WriteRoute(commitId))
            }
        )
    }
}
