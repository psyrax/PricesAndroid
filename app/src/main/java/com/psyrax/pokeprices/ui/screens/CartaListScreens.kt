package com.psyrax.pokeprices.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.psyrax.pokeprices.data.model.CartaWithVariants
import com.psyrax.pokeprices.ui.util.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForSaleScreen(
    cartas: List<CartaWithVariants>,
    usdToMxnRate: Double,
    onAddCarta: () -> Unit,
    onEditCarta: (String) -> Unit,
    onViewDetail: (String) -> Unit,
    onDeleteCarta: (CartaWithVariants) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("En Venta") },
                actions = {
                    IconButton(onClick = onAddCarta) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                }
            )
        }
    ) { padding ->
        if (cartas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Sell,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sin cartas en venta", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Agrega cartas con el botón +",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(cartas, key = { it.carta.id }) { cartaWithVariants ->
                    CartaListItem(
                        cartaWithVariants = cartaWithVariants,
                        usdToMxnRate = usdToMxnRate,
                        showTagId = true,
                        onEdit = { onEditCarta(cartaWithVariants.carta.id) },
                        onView = { onViewDetail(cartaWithVariants.carta.id) },
                        onDelete = { onDeleteCarta(cartaWithVariants) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WantToBuyScreen(
    cartas: List<CartaWithVariants>,
    usdToMxnRate: Double,
    onAddCarta: () -> Unit,
    onEditCarta: (String) -> Unit,
    onViewDetail: (String) -> Unit,
    onDeleteCarta: (CartaWithVariants) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiero Comprar") },
                actions = {
                    IconButton(onClick = onAddCarta) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                }
            )
        }
    ) { padding ->
        if (cartas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sin cartas en la lista", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Agrega cartas que quieras comprar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(cartas, key = { it.carta.id }) { cartaWithVariants ->
                    CartaListItem(
                        cartaWithVariants = cartaWithVariants,
                        usdToMxnRate = usdToMxnRate,
                        showTagId = false,
                        onEdit = { onEditCarta(cartaWithVariants.carta.id) },
                        onView = { onViewDetail(cartaWithVariants.carta.id) },
                        onDelete = { onDeleteCarta(cartaWithVariants) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartaListItem(
    cartaWithVariants: CartaWithVariants,
    usdToMxnRate: Double,
    showTagId: Boolean,
    onEdit: () -> Unit,
    onView: () -> Unit,
    onDelete: () -> Unit
) {
    val carta = cartaWithVariants.carta
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar carta") },
            text = { Text("¿Estás seguro de que quieres eliminar \"${carta.name}\"?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onView() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tag ID
            if (showTagId && !carta.tagId.isNullOrEmpty()) {
                Text(
                    text = carta.tagId!!,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(50.dp)
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(carta.name, style = MaterialTheme.typography.titleSmall)
                val subtitle = carta.expansionName ?: carta.expansionCode
                Text(
                    "$subtitle #${carta.cardNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Prices
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    PriceFormatter.formatUSD(carta.price),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    PriceFormatter.formatMXN(carta.price, usdToMxnRate),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4CAF50)
                )
            }

            // Actions
            IconButton(onClick = onView) {
                Icon(Icons.Default.Visibility, contentDescription = "Ver", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
