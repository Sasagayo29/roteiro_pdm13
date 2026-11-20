package com.example.roteiro_pdm13.ui.navigation

sealed class Screen(val route: String) {
    data object MaintenanceList : Screen("maintenance_list")
    data object MaintenanceTrash : Screen("maintenance_trash")
    data object MaintenanceForm : Screen("maintenance_form?itemId={itemId}") {
        fun createRoute(itemId: Long? = null): String {
            return if (itemId != null && itemId > 0L) {
                "maintenance_form?itemId=$itemId"
            } else {
                "maintenance_form?itemId=-1"
            }
        }
    }
}