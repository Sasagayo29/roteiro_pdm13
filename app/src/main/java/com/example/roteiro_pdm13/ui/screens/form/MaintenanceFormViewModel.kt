package com.example.roteiro_pdm13.ui.screens.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roteiro_pdm13.domain.model.MaintenanceItem
import com.example.roteiro_pdm13.domain.usecase.CheckNotificationPermissionUseCase
import com.example.roteiro_pdm13.domain.usecase.GetMaintenanceByIdUseCase
import com.example.roteiro_pdm13.domain.usecase.SaveMaintenanceUseCase
import com.example.roteiro_pdm13.domain.usecase.SendMaintenanceNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MaintenanceFormUiState(
    val id: Long = 0L,
    val veiculo: String = "",
    val servico: String = "",
    val categoria: String = "Motor",
    val quilometragemAlvo: String = "",
    val dataPrevista: String = "",
    val observacoes: String = "",
    val notifyOnSave: Boolean = true,
    val errorMessage: String? = null,
    val isSavedSuccess: Boolean = false
) {
    val isEditMode: Boolean get() = id > 0L
}

@HiltViewModel
class MaintenanceFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMaintenanceByIdUseCase: GetMaintenanceByIdUseCase,
    private val saveMaintenanceUseCase: SaveMaintenanceUseCase,
    private val checkNotificationPermissionUseCase: CheckNotificationPermissionUseCase,
    private val sendMaintenanceNotificationUseCase: SendMaintenanceNotificationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaintenanceFormUiState())
    val uiState: StateFlow<MaintenanceFormUiState> = _uiState.asStateFlow()

    init {
        val itemId = savedStateHandle.get<Long>("itemId") ?: -1L
        if (itemId > 0L) {
            loadItem(itemId)
        }
    }

    private fun loadItem(id: Long) {
        viewModelScope.launch {
            getMaintenanceByIdUseCase(id)?.let { item ->
                _uiState.update {
                    it.copy(
                        id = item.id,
                        veiculo = item.veiculo,
                        servico = item.servico,
                        categoria = item.categoria,
                        quilometragemAlvo = item.quilometragemAlvo,
                        dataPrevista = item.dataPrevista,
                        observacoes = item.observacoes
                    )
                }
            }
        }
    }

    fun hasNotificationPermission(): Boolean = checkNotificationPermissionUseCase()

    fun onFieldChange(updateBlock: (MaintenanceFormUiState) -> MaintenanceFormUiState) {
        _uiState.update { current ->
            updateBlock(current).copy(errorMessage = null)
        }
    }

    fun saveItem() {
        val state = _uiState.value
        viewModelScope.launch {
            val item = MaintenanceItem(
                id = state.id,
                veiculo = state.veiculo.trim(),
                servico = state.servico.trim(),
                categoria = state.categoria,
                quilometragemAlvo = state.quilometragemAlvo.trim(),
                dataPrevista = state.dataPrevista.trim(),
                observacoes = state.observacoes.trim()
            )

            saveMaintenanceUseCase(item).fold(
                onSuccess = { savedId ->
                    if (state.notifyOnSave) {
                        sendMaintenanceNotificationUseCase(
                            item = item.copy(id = savedId),
                            isNewOrUpdated = true
                        )
                    }
                    _uiState.update { it.copy(isSavedSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
            )
        }
    }
}