package com.psyrax.pokeprices.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.psyrax.pokeprices.data.model.CartaWithVariants
import com.psyrax.pokeprices.ui.util.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartaDetailScreen(
    cartaWithVariants: CartaWithVariants,
    usdToMxnRate: Double,
    onDismiss: () -> Unit
) {
    val carta = cartaWithVariants.carta
    val variants = cartaWithVariants.variants

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Card image
            if (!carta.imageURL.isNullOrEmpty()) {
                AsyncImage(
                    model = carta.imageURL,
                    contentDescription = carta.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Name
            Text(carta.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            // Info
            Text("Expansión: ${carta.expansionName ?: carta.expansionCode}")
            Text("Número: ${carta.cardNumber}")

            carta.game?.let { Text("Juego: $it") }
            carta.rarity?.let { Text("Rareza: $it") }
            carta.apiCardId?.let {
                Text(
                    "API ID: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prices
            Text(
                "Precio USD: ${PriceFormatter.formatUSD(carta.price)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Precio MXN: ${PriceFormatter.formatMXN(carta.price, usdToMxnRate)}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF4CAF50)
            )

            // Variants
            if (variants.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Variantes (${variants.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                variants.forEach { variant ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    variant.condition,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        PriceFormatter.formatUSD(variant.price),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        PriceFormatter.formatMXN(variant.price, usdToMxnRate),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                            Text(
                                variant.printing,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Actualizado: ${PriceFormatter.formatDate(variant.lastUpdated)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
