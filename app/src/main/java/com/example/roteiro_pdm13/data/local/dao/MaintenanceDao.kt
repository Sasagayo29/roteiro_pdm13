package com.example.roteiro_pdm13.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.roteiro_pdm13.data.local.entity.MaintenanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {

    @Query("SELECT * FROM maintenance_items WHERE is_deleted = 0 ORDER BY id DESC")
    fun getActiveItems(): Flow<List<MaintenanceEntity>>

    @Query("SELECT * FROM maintenance_items WHERE is_deleted = 1 ORDER BY id DESC")
    fun getDeletedItems(): Flow<List<MaintenanceEntity>>

    @Query("SELECT * FROM maintenance_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): MaintenanceEntity?

    // Insere novo registro ou atualiza se já existir (Edição)
    @Upsert
    suspend fun upsertItem(item: MaintenanceEntity): Long

    // Soft Delete: marca como excluído logicamente
    @Query("UPDATE maintenance_items SET is_deleted = 1 WHERE id = :id")
    suspend fun softDeleteById(id: Long)

    // Restaura registro excluído logicamente
    @Query("UPDATE maintenance_items SET is_deleted = 0 WHERE id = :id")
    suspend fun restoreById(id: Long)
}