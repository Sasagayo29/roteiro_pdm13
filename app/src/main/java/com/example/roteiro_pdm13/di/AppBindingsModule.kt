package com.example.roteiro_pdm13.di

import com.example.roteiro_pdm13.data.notification.AndroidNotificationService
import com.example.roteiro_pdm13.data.repository.MaintenanceRepositoryImpl
import com.example.roteiro_pdm13.domain.repository.MaintenanceRepository
import com.example.roteiro_pdm13.domain.service.NotificationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {

    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(
        impl: MaintenanceRepositoryImpl
    ): MaintenanceRepository

    @Binds
    @Singleton
    abstract fun bindNotificationService(
        impl: AndroidNotificationService
    ): NotificationService
}