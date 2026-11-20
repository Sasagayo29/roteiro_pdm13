package com.example.roteiro_pdm13.di

import android.content.Context
import androidx.room.Room
import com.example.roteiro_pdm13.data.local.dao.MaintenanceDao
import com.example.roteiro_pdm13.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "autocare_pdm13.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMaintenanceDao(database: AppDatabase): MaintenanceDao {
        return database.maintenanceDao()
    }
}