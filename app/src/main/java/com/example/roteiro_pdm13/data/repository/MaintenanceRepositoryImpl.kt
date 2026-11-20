package com.example.roteiro_pdm13.data.repository

import com.example.roteiro_pdm13.data.local.dao.MaintenanceDao
import com.example.roteiro_pdm13.data.mapper.toDomain
import com.example.roteiro_pdm13.data.mapper.toEntity
import com.example.roteiro_pdm13.domain.model.MaintenanceItem
import com.example.roteiro_pdm13.domain.repository.MaintenanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MaintenanceRepositoryImpl @Inject constructor(
    private val dao: MaintenanceDao
) : MaintenanceRepository {

    override fun getActiveItems(): Flow<List<MaintenanceItem>> =
        dao.getActiveItems().map { list -> list.map { it.toDomain() } }

    override fun getDeletedItems(): Flow<List<MaintenanceItem>> =
        dao.getDeletedItems().map { list -> list.map { it.toDomain() } }

    override suspend fun getItemById(id: Long): MaintenanceItem? =
        dao.getItemById(id)?.toDomain()

    override suspend fun saveItem(item: MaintenanceItem): Long {
        val resultId = dao.upsertItem(item.toEntity())
        return if (resultId == -1L) item.id else resultId
    }

    override suspend fun softDeleteItem(id: Long) = dao.softDeleteById(id)

    override suspend fun restoreItem(id: Long) = dao.restoreById(id)
}