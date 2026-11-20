package com.example.roteiro_pdm13.domain.repository

import com.example.roteiro_pdm13.domain.model.MaintenanceItem
import kotlinx.coroutines.flow.Flow

interface MaintenanceRepository {
    fun getActiveItems(): Flow<List<MaintenanceItem>>
    fun getDeletedItems(): Flow<List<MaintenanceItem>>
    suspend fun getItemById(id: Long): MaintenanceItem?
    suspend fun saveItem(item: MaintenanceItem): Long
    suspend fun softDeleteItem(id: Long)
    suspend fun restoreItem(id: Long)
}