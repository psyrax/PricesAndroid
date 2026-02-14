package com.psyrax.pokeprices.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.psyrax.pokeprices.data.model.CartaWithVariants
import com.psyrax.pokeprices.data.model.GameSet
import com.psyrax.pokeprices.ui.util.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchText: String,
    selectedSet: GameSet?,
    sets: List<GameSet>,
    isSearching: Boolean,
    results: List<CartaWithVariants>,
    errorMessage: String?,
    hasSearched: Boolean,
    apiKey: String,
    usdToMxnRate: Double,
    onSearchTextChange: (String) -> Unit,
    onSetSelected: (GameSet?) -> Unit,
    onSearch: () -> Unit,
    onCardSelected: (CartaWithVariants) -> Unit
) {
    var showSetDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buscar Precios") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search form
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Search field
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    placeholder = { Text("Nombre de la carta...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { onSearchTextChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Set picker
                ExposedDropdownMenuBox(
                    expanded = showSetDropdown,
                    onExpandedChange = { showSetDropdown = !showSetDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedSet?.let { "${it.name} (${it.releaseDate?.take(4) ?: ""})" } ?: "Todas las expansiones",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Expansión (opcional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSetDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showSetDropdown,
                        onDismissRequest = { showSetDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas las expansiones") },
                            onClick = { onSetSelected(null); showSetDropdown = false }
                        )
                        sets.forEach { set ->
                            DropdownMenuItem(
                                text = { Text("${set.name} (${set.releaseDate?.take(4) ?: ""})") },
                                onClick = { onSetSelected(set); showSetDropdown = false }
                            )
                        }
                    }
                }

                // Search button
                Button(
                    onClick = onSearch,
                    enabled = searchText.isNotBlank() && !isSearching && apiKey.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buscando...")
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buscar Precios")
                    }
                }
            }

            // Results area
            when {
                apiKey.isEmpty() -> {
                    EmptyState(
                        icon = "🔑",
                        title = "API Key requerida",
                        subtitle = "Configura tu API key en Configuración para buscar."
                    )
                }
                errorMessage != null -> {
                    EmptyState(icon = "⚠️", title = "Error", subtitle = errorMessage)
                }
                hasSearched && results.isEmpty() -> {
                    EmptyState(
                        icon = "🔍",
                        title = "Sin resultados",
                        subtitle = "No se encontraron cartas para \"$searchText\""
                    )
                }
                !hasSearched -> {
                    EmptyState(
                        icon = "✨",
                        title = "Busca una carta",
                        subtitle = "Escribe el nombre de una carta para consultar precios."
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(results) { cartaWithVariants ->
                            SearchResultRow(
                                cartaWithVariants = cartaWithVariants,
                                usdToMxnRate = usdToMxnRate,
                                onClick = { onCardSelected(cartaWithVariants) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    cartaWithVariants: CartaWithVariants,
    usdToMxnRate: Double,
    onClick: () -> Unit
) {
    val carta = cartaWithVariants.carta

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            if (!carta.imageURL.isNullOrEmpty()) {
                AsyncImage(
                    model = carta.imageURL,
                    contentDescription = carta.name,
                    modifier = Modifier
                        .size(50.dp, 70.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(carta.name, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        carta.expansionName ?: carta.expansionCode,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        "#${carta.cardNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                carta.rarity?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = Color(0xFFFF9800))
                }
                if (cartaWithVariants.variants.isNotEmpty()) {
                    Text(
                        "${cartaWithVariants.variants.size} variante(s)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Prices
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    PriceFormatter.formatUSD(carta.price),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    PriceFormatter.formatMXN(carta.price, usdToMxnRate),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(icon: String, title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
