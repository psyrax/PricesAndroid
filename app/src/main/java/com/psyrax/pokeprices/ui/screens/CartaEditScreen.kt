package com.psyrax.pokeprices.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.psyrax.pokeprices.data.model.CartaWithVariants
import com.psyrax.pokeprices.data.model.GameSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartaEditScreen(
    cartaWithVariants: CartaWithVariants?,
    sets: List<GameSet>,
    isFetching: Boolean,
    fetchMessage: String?,
    foundCards: List<CartaWithVariants>,
    onUpdateCarta: (name: String, expansionCode: String, expansionName: String?, cardNumber: String, priceText: String, currency: String, tagId: String, imageURL: String) -> Unit,
    onFetchCardInfo: (name: String, setId: String) -> Unit,
    onSelectCard: (CartaWithVariants) -> Unit,
    onBack: () -> Unit
) {
    if (cartaWithVariants == null) return

    val carta = cartaWithVariants.carta
    var name by remember(carta.id) { mutableStateOf(carta.name) }
    var cardNumber by remember(carta.id) { mutableStateOf(carta.cardNumber) }
    var priceText by remember(carta.id) { mutableStateOf(carta.price?.toString() ?: "") }
    var currency by remember(carta.id) { mutableStateOf(carta.currency ?: "USD") }
    var tagId by remember(carta.id) { mutableStateOf(carta.tagId ?: "") }
    var imageURL by remember(carta.id) { mutableStateOf(carta.imageURL ?: "") }
    var selectedSet by remember(carta.id) { mutableStateOf<GameSet?>(sets.find { it.id == carta.expansionCode }) }
    var showSetDropdown by remember { mutableStateOf(false) }
    var showCardSelection by remember { mutableStateOf(false) }

    // Update fields when carta changes from API fetch
    LaunchedEffect(carta) {
        name = carta.name
        cardNumber = carta.cardNumber
        priceText = carta.price?.toString() ?: ""
        currency = carta.currency ?: "USD"
        tagId = carta.tagId ?: ""
        imageURL = carta.imageURL ?: ""
        selectedSet = sets.find { it.id == carta.expansionCode }
    }

    LaunchedEffect(foundCards) {
        if (foundCards.isNotEmpty()) showCardSelection = true
    }

    if (showCardSelection && foundCards.isNotEmpty()) {
        CardSelectionDialog(
            cards = foundCards,
            onSelect = { selected ->
                onSelectCard(selected)
                showCardSelection = false
            },
            onDismiss = { showCardSelection = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar carta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        onUpdateCarta(
                            name,
                            selectedSet?.id ?: carta.expansionCode,
                            selectedSet?.name,
                            cardNumber,
                            priceText,
                            currency,
                            tagId,
                            imageURL
                        )
                        onBack()
                    }) {
                        Text("Guardar")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Datos
            Text("Datos", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            // Expansion picker
            ExposedDropdownMenuBox(
                expanded = showSetDropdown,
                onExpandedChange = { showSetDropdown = !showSetDropdown }
            ) {
                OutlinedTextField(
                    value = selectedSet?.let { "${it.name} (${it.releaseDate?.take(4) ?: ""})" } ?: "Selecciona expansión",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Expansión") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSetDropdown) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = showSetDropdown,
                    onDismissRequest = { showSetDropdown = false }
                ) {
                    sets.forEach { set ->
                        DropdownMenuItem(
                            text = { Text("${set.name} (${set.releaseDate?.take(4) ?: ""})") },
                            onClick = {
                                selectedSet = set
                                showSetDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = { Text("Número de carta") },
                modifier = Modifier.fillMaxWidth()
            )

            // Fetch from API button
            Button(
                onClick = { selectedSet?.let { onFetchCardInfo(name, it.id) } },
                enabled = selectedSet != null && name.isNotBlank() && !isFetching,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isFetching) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obteniendo información...")
                } else {
                    Icon(Icons.Default.CloudDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obtener Información del API")
                }
            }

            fetchMessage?.let {
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = if (it.startsWith("✅")) MaterialTheme.colorScheme.primary
                    else if (it.startsWith("❌")) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Image section
            HorizontalDivider()
            Text("Imagen", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = imageURL,
                onValueChange = { imageURL = it },
                label = { Text("URL de imagen") },
                modifier = Modifier.fillMaxWidth()
            )

            if (imageURL.isNotBlank()) {
                AsyncImage(
                    model = imageURL,
                    contentDescription = "Card image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // Price section
            HorizontalDivider()
            Text("Precio", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Precio") },
                    modifier = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Moneda") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Tag NFC
            HorizontalDivider()
            Text("Tag NFC", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = tagId,
                onValueChange = { tagId = it },
                label = { Text("Tag ID (ogl://card?id=X)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (tagId.isNotBlank()) {
                Text(
                    "URL: ogl://card?id=$tagId",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CardSelectionDialog(
    cards: List<CartaWithVariants>,
    onSelect: (CartaWithVariants) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona una carta") },
        text = {
            Column {
                cards.forEach { card ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelect(card) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(card.carta.name, style = MaterialTheme.typography.bodyLarge)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                card.carta.rarity?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                                card.carta.price?.let {
                                    Text(
                                        "$${String.format("%.2f", it)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            if (card.variants.isNotEmpty()) {
                                Text(
                                    "${card.variants.size} variante(s)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
