package com.example.roteiro_pdm13.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roteiro_pdm13.domain.model.MaintenanceItem
import com.example.roteiro_pdm13.domain.usecase.CheckNotificationPermissionUseCase
import com.example.roteiro_pdm13.domain.usecase.GetActiveMaintenancesUseCase
import com.example.roteiro_pdm13.domain.usecase.SendMaintenanceNotificationUseCase
import com.example.roteiro_pdm13.domain.usecase.SoftDeleteMaintenanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaintenanceListViewModel @Inject constructor(
    getActiveMaintenancesUseCase: GetActiveMaintenancesUseCase,
    private val softDeleteMaintenanceUseCase: SoftDeleteMaintenanceUseCase,
    private val checkNotificationPermissionUseCase: CheckNotificationPermissionUseCase,
    private val sendMaintenanceNotificationUseCase: SendMaintenanceNotificationUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _hasPermission = MutableStateFlow(checkNotificationPermissionUseCase())
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    val items: StateFlow<List<MaintenanceItem>> = combine(
        getActiveMaintenancesUseCase(),
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter {
                it.veiculo.contains(query, ignoreCase = true) ||
                        it.servico.contains(query, ignoreCase = true) ||
                        it.categoria.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun refreshPermissionStatus() {
        _hasPermission.value = checkNotificationPermissionUseCase()
    }

    fun onPermissionResult(isGranted: Boolean) {
        _hasPermission.value = isGranted
        _feedbackMessage.value = if (isGranted) {
            "Permissão concedida! As notificações estão ativadas."
        } else {
            "Permissão negada. Ative nas configurações para receber alertas."
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun triggerNotification(item: MaintenanceItem) {
        val sent = sendMaintenanceNotificationUseCase(item, isNewOrUpdated = false)
        _feedbackMessage.value = if (sent) {
            "Notificação enviada: ${item.servico}"
        } else {
            "Conceda a permissão de notificações primeiro."
        }
    }

    fun softDeleteItem(id: Long) {
        viewModelScope.launch {
            softDeleteMaintenanceUseCase(id)
            _feedbackMessage.value = "Registro movido para a lixeira (Soft Delete)."
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }
}