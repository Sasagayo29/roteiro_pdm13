package com.example.roteiro_pdm13.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.roteiro_pdm13.ui.screens.form.MaintenanceFormScreen
import com.example.roteiro_pdm13.ui.screens.list.MaintenanceListScreen
import com.example.roteiro_pdm13.ui.screens.trash.MaintenanceTrashScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.MaintenanceList.route
    ) {
        composable(route = Screen.MaintenanceList.route) {
            MaintenanceListScreen(
                onAddItem = {
                    navController.navigate(Screen.MaintenanceForm.createRoute(null))
                },
                onEditItem = { id ->
                    navController.navigate(Screen.MaintenanceForm.createRoute(id))
                },
                onOpenTrash = {
                    navController.navigate(Screen.MaintenanceTrash.route)
                }
            )
        }

        composable(
            route = Screen.MaintenanceForm.route,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            MaintenanceFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.MaintenanceTrash.route) {
            MaintenanceTrashScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}