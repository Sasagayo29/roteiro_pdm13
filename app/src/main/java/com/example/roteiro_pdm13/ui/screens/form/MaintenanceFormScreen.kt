package com.example.roteiro_pdm13.ui.screens.form

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: MaintenanceFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val categories = listOf("Motor", "Pneus", "Freios", "Relação", "Elétrica", "Documentação")

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        // Salva logo após o usuário responder ao diálogo de permissão do Android
        viewModel.saveItem()
    }

    LaunchedEffect(state.isSavedSuccess) {
        if (state.isSavedSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isEditMode) "Editar Manutenção" else "Nova Manutenção")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.veiculo,
                onValueChange = { v -> viewModel.onFieldChange { it.copy(veiculo = v) } },
                label = { Text("Veículo / Modelo *") },
                placeholder = { Text("Ex: Moto 300cc, Hatch 1.6...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.servico,
                onValueChange = { v -> viewModel.onFieldChange { it.copy(servico = v) } },
                label = { Text("Serviço de Manutenção *") },
                placeholder = { Text("Ex: Troca de óleo e filtro") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(text = "Categoria", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = state.categoria == cat,
                        onClick = { viewModel.onFieldChange { it.copy(categoria = cat) } },
                        label = { Text(cat) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.quilometragemAlvo,
                    onValueChange = { v -> viewModel.onFieldChange { it.copy(quilometragemAlvo = v) } },
                    label = { Text("Meta (KM)") },
                    placeholder = { Text("Ex: 15.000 km") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.dataPrevista,
                    onValueChange = { v -> viewModel.onFieldChange { it.copy(dataPrevista = v) } },
                    label = { Text("Data Prevista") },
                    placeholder = { Text("Ex: 30/11/2026") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.observacoes,
                onValueChange = { v -> viewModel.onFieldChange { it.copy(observacoes = v) } },
                label = { Text("Observações / Oficina") },
                placeholder = { Text("Especificação do óleo, peças, orçamento...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notificar ao salvar",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Dispara alerta com ícone, título e descrição",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = state.notifyOnSave,
                        onCheckedChange = { checked ->
                            viewModel.onFieldChange { it.copy(notifyOnSave = checked) }
                        }
                    )
                }
            }

            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    if (state.notifyOnSave &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        !viewModel.hasNotificationPermission()
                    ) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.saveItem()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (state.isEditMode) "Salvar Alterações" else "Cadastrar Manutenção")
            }
        }
    }
}