package com.psyrax.pokeprices.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psyrax.pokeprices.PokePricesApp
import com.psyrax.pokeprices.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartaEditViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as PokePricesApp
    private val repository = app.cartaRepository
    val settingsDataStore = app.settingsDataStore
    val sets: Flow<List<GameSet>> = repository.getAllSets()

    private val _carta = MutableStateFlow<CartaWithVariants?>(null)
    val carta: StateFlow<CartaWithVariants?> = _carta

    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> = _isFetching

    private val _fetchMessage = MutableStateFlow<String?>(null)
    val fetchMessage: StateFlow<String?> = _fetchMessage

    private val _foundCards = MutableStateFlow<List<CartaWithVariants>>(emptyList())
    val foundCards: StateFlow<List<CartaWithVariants>> = _foundCards

    fun loadCarta(cartaId: String) {
        viewModelScope.launch {
            _carta.value = repository.getCartaById(cartaId)
        }
    }

    fun updateCarta(carta: Carta, variants: List<CartaVariant> = emptyList()) {
        viewModelScope.launch {
            repository.updateCarta(carta, variants)
            _carta.value = repository.getCartaById(carta.id)
        }
    }

    fun fetchCardInfo(name: String, setId: String, apiKey: String) {
        viewModelScope.launch {
            _isFetching.value = true
            _fetchMessage.value = null
            try {
                val results = repository.searchCardsByNameAndSet(name, setId, apiKey)
                if (results.isEmpty()) {
                    _fetchMessage.value = "⚠️ No se encontraron cartas"
                } else if (results.size == 1) {
                    _foundCards.value = emptyList()
                    _fetchMessage.value = "✅ Información actualizada (${results[0].variants.size} variantes)"
                    applyCardData(results[0])
                } else {
                    _foundCards.value = results
                    _fetchMessage.value = "🔍 Se encontraron ${results.size} cartas, selecciona una"
                }
            } catch (e: Exception) {
                _fetchMessage.value = "❌ Error: ${e.message}"
            }
            _isFetching.value = false
        }
    }

    fun applyCardData(from: CartaWithVariants) {
        val current = _carta.value ?: return
        val updated = current.carta.copy(
            apiId = from.carta.apiId,
            apiCardId = from.carta.apiCardId,
            name = from.carta.name,
            game = from.carta.game,
            expansionCode = from.carta.expansionCode,
            expansionName = from.carta.expansionName,
            cardNumber = from.carta.cardNumber,
            rarity = from.carta.rarity,
            tcgplayerId = from.carta.tcgplayerId,
            details = from.carta.details,
            imageURL = from.carta.imageURL,
            price = from.carta.price,
            currency = from.carta.currency
        )
        val variants = from.variants.map { it.copy(cartaId = current.carta.id) }
        updateCarta(updated, variants)
        _foundCards.value = emptyList()
        _fetchMessage.value = "✅ Información actualizada (${variants.size} variantes)"
    }
}
