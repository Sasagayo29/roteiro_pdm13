package com.example.roteiro_pdm13.domain.service

interface NotificationService {
    fun hasNotificationPermission(): Boolean
    fun showMaintenanceAlert(
        notificationId: Int,
        title: String,
        description: String
    ): Boolean
}