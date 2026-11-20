package com.example.roteiro_pdm13.domain.usecase

import com.example.roteiro_pdm13.domain.model.MaintenanceItem
import com.example.roteiro_pdm13.domain.repository.MaintenanceRepository
import com.example.roteiro_pdm13.domain.service.NotificationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveMaintenancesUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    operator fun invoke(): Flow<List<MaintenanceItem>> = repository.getActiveItems()
}

class GetDeletedMaintenancesUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    operator fun invoke(): Flow<List<MaintenanceItem>> = repository.getDeletedItems()
}

class GetMaintenanceByIdUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(id: Long): MaintenanceItem? = repository.getItemById(id)
}

class SaveMaintenanceUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(item: MaintenanceItem): Result<Long> {
        if (item.veiculo.isBlank()) {
            return Result.failure(IllegalArgumentException("Informe o nome/modelo do veículo."))
        }
        if (item.servico.isBlank()) {
            return Result.failure(IllegalArgumentException("Informe o serviço de manutenção."))
        }
        val savedId = repository.saveItem(item)
        return Result.success(savedId)
    }
}

class SoftDeleteMaintenanceUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(id: Long) = repository.softDeleteItem(id)
}

class RestoreMaintenanceUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(id: Long) = repository.restoreItem(id)
}

class CheckNotificationPermissionUseCase @Inject constructor(
    private val notificationService: NotificationService
) {
    operator fun invoke(): Boolean = notificationService.hasNotificationPermission()
}

class SendMaintenanceNotificationUseCase @Inject constructor(
    private val notificationService: NotificationService
) {
    operator fun invoke(item: MaintenanceItem, isNewOrUpdated: Boolean = false): Boolean {
        val prefixo = if (isNewOrUpdated) "Manutenção Agendada" else "Lembrete de Revisão"
        val title = "$prefixo: ${item.servico}"
        val detalhes = buildString {
            append("Veículo: ${item.veiculo} (${item.categoria})")
            if (item.quilometragemAlvo.isNotBlank()) append(" • Meta: ${item.quilometragemAlvo}")
            if (item.dataPrevista.isNotBlank()) append(" • Data: ${item.dataPrevista}")
            if (item.observacoes.isNotBlank()) append("\nObs: ${item.observacoes}")
        }
        val notificationId = if (item.id > 0L) item.id.toInt() else System.currentTimeMillis().toInt()
        return notificationService.showMaintenanceAlert(
            notificationId = notificationId,
            title = title,
            description = detalhes
        )
    }
}