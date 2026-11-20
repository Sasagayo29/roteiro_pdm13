package com.example.roteiro_pdm13.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.roteiro_pdm13.data.local.dao.MaintenanceDao
import com.example.roteiro_pdm13.data.local.entity.MaintenanceEntity

@Database(
    entities = [MaintenanceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun maintenanceDao(): MaintenanceDao
}