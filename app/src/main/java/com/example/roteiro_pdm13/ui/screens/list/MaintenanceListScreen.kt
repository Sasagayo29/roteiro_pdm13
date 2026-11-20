package com.example.roteiro_pdm13.ui.screens.list

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roteiro_pdm13.domain.model.MaintenanceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceListScreen(
    onAddItem: () -> Unit,
    onEditItem: (Long) -> Unit,
    onOpenTrash: () -> Unit,
    viewModel: MaintenanceListViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val hasPermission by viewModel.hasPermission.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var pendingItemToNotify by remember { mutableStateOf<MaintenanceItem?>(null) }

    // Lançador oficial de permissão em tempo de execução (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
        if (isGranted) {
            pendingItemToNotify?.let { item ->
                viewModel.triggerNotification(item)
            }
        }
        pendingItemToNotify = null
    }

    LaunchedEffect(Unit) {
        viewModel.refreshPermissionStatus()
    }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    fun requestNotificationPermission(itemAfterGrant: MaintenanceItem? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pendingItemToNotify = itemAfterGrant
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.refreshPermissionStatus()
            itemAfterGrant?.let { viewModel.triggerNotification(it) }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("AutoCare • Revisões") },
                actions = {
                    IconButton(onClick = onOpenTrash) {
                        Icon(
                            imageVector = Icons.Default.RestoreFromTrash,
                            contentDescription = "Lixeira (Soft Delete)"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddItem,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nova Revisão") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Card de status da permissão em tempo de execução
            PermissionStatusBanner(
                hasPermission = hasPermission,
                onRequestPermission = { requestNotificationPermission(null) }
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Filtrar por veículo, serviço ou categoria...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhuma manutenção cadastrada.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp, top = 6.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        MaintenanceCard(
                            item = item,
                            onNotify = {
                                if (hasPermission) {
                                    viewModel.triggerNotification(item)
                                } else {
                                    requestNotificationPermission(item)
                                }
                            },
                            onEdit = { onEditItem(item.id) },
                            onSoftDelete = { viewModel.softDeleteItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionStatusBanner(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val containerColor = if (hasPermission) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (hasPermission) {
                        Icons.Default.NotificationsActive
                    } else {
                        Icons.Default.NotificationsOff
                    },
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (hasPermission) "Notificações Permitidas" else "Permissão Pendente",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (hasPermission) {
                            "Toque no sino em qualquer item para testar o alerta."
                        } else {
                            "Autorize POST_NOTIFICATIONS para disparar lembretes."
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (!hasPermission) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRequestPermission) {
                    Text("Permitir")
                }
            }
        }
    }
}

@Composable
private fun MaintenanceCard(
    item: MaintenanceItem,
    onNotify: () -> Unit,
    onEdit: () -> Unit,
    onSoftDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.servico,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.veiculo} • ${item.categoria}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row {
                    IconButton(onClick = onNotify) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Disparar Notificação",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onSoftDelete) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Mover para Lixeira (Soft Delete)",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (item.quilometragemAlvo.isNotBlank() || item.dataPrevista.isNotBlank()) {
                Text(
                    text = "Meta: ${item.quilometragemAlvo.ifBlank { "-" }}  |  Data prevista: ${item.dataPrevista.ifBlank { "-" }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (item.observacoes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.observacoes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}