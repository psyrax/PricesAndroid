package com.psyrax.pokeprices.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    apiKey: String,
    apiKeyStatus: String?,
    isRefreshingSets: Boolean,
    usdToMxnRate: Double,
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
    val uriHandler = LocalUriHandler.current

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
                    // Estado de verificación automática
                    if (apiKeyStatus != null) {
                        val isVerifying = apiKeyStatus == "Verificando..."
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isVerifying) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                            }
                            Text(
                                text = apiKeyStatus,
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    isVerifying -> MaterialTheme.colorScheme.onSurfaceVariant
                                    apiKeyStatus.startsWith("✅") -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    }
                    val linkText = buildAnnotatedString {
                        append("Obtén tu clave en ")
                        pushStringAnnotation(tag = "URL", annotation = "https://justtcg.com/")
                        withStyle(SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )) {
                            append("justtcg.com")
                        }
                        pop()
                    }
                    ClickableText(
                        text = linkText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = { offset ->
                            linkText.getStringAnnotations("URL", offset, offset)
                                .firstOrNull()?.let { uriHandler.openUri(it.item) }
                        }
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
