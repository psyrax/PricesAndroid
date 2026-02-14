package com.psyrax.pokeprices.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    apiKey: String,
    usdToMxnRate: Double,
    isRefreshingSets: Boolean,
    refreshMessage: String?,
    isUpdatingCards: Boolean,
    updateProgress: String?,
    isUpdatingRate: Boolean,
    rateUpdateMessage: String?,
    cartaCount: Int,
    onApiKeyChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onRefreshSets: () -> Unit,
    onUpdateAllCards: () -> Unit,
    onUpdateExchangeRate: () -> Unit
) {
    var rateText by remember(usdToMxnRate) { mutableStateOf(String.format("%.2f", usdToMxnRate)) }
    var localApiKey by remember(apiKey) { mutableStateOf(apiKey) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Configuración") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API Key section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("JustTCG API", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = localApiKey,
                        onValueChange = {
                            localApiKey = it
                            onApiKeyChange(it)
                        },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(
                        "Obtén tu clave en justtcg.com/dashboard/plans",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Exchange rate section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tipo de Cambio", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("USD a MXN:", modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = rateText,
                            onValueChange = {
                                rateText = it
                                it.toDoubleOrNull()?.let { rate -> onRateChange(it) }
                            },
                            modifier = Modifier.width(100.dp),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = onUpdateExchangeRate,
                        enabled = !isUpdatingRate,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isUpdatingRate) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Obteniendo tasa...")
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar Tasa Automáticamente")
                        }
                    }

                    rateUpdateMessage?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall,
                            color = if (it.startsWith("✅")) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Sets section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Expansiones", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = onRefreshSets,
                        enabled = !isRefreshingSets && apiKey.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isRefreshingSets) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizando...")
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar Sets")
                        }
                    }
                    refreshMessage?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Cards update section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cartas", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = onUpdateAllCards,
                        enabled = !isUpdatingCards && apiKey.isNotEmpty() && cartaCount > 0,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isUpdatingCards) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizando...")
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar Todas las Cartas")
                        }
                    }
                    updateProgress?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "Total de cartas: $cartaCount",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
