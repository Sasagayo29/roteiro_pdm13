package com.example.roteiro_pdm13.ui.screens.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roteiro_pdm13.domain.model.MaintenanceItem
import com.example.roteiro_pdm13.domain.usecase.GetDeletedMaintenancesUseCase
import com.example.roteiro_pdm13.domain.usecase.RestoreMaintenanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaintenanceTrashViewModel @Inject constructor(
    getDeletedMaintenancesUseCase: GetDeletedMaintenancesUseCase,
    private val restoreMaintenanceUseCase: RestoreMaintenanceUseCase
) : ViewModel() {

    val deletedItems: StateFlow<List<MaintenanceItem>> = getDeletedMaintenancesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun restoreItem(id: Long) {
        viewModelScope.launch {
            restoreMaintenanceUseCase(id)
        }
    }
}