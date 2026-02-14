package com.psyrax.pokeprices.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psyrax.pokeprices.PokePricesApp
import com.psyrax.pokeprices.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class CartaListViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as PokePricesApp
    private val repository = app.cartaRepository
    val settingsDataStore = app.settingsDataStore

    val forSaleCartas: Flow<List<CartaWithVariants>> = repository.getForSaleCartas()
    val wantToBuyCartas: Flow<List<CartaWithVariants>> = repository.getWantToBuyCartas()
    val usdToMxnRate: Flow<Double> = settingsDataStore.usdToMxnRate

    private val _deepLinkCardId = MutableStateFlow<String?>(null)
    val deepLinkCardId: StateFlow<String?> = _deepLinkCardId

    private val _deepLinkCarta = MutableStateFlow<CartaWithVariants?>(null)
    val deepLinkCarta: StateFlow<CartaWithVariants?> = _deepLinkCarta

    fun handleDeepLink(cardId: String) {
        viewModelScope.launch {
            _deepLinkCardId.value = cardId
            val carta = repository.getCartaByTagId(cardId)
            _deepLinkCarta.value = carta
        }
    }

    fun clearDeepLink() {
        _deepLinkCardId.value = null
        _deepLinkCarta.value = null
    }

    fun addNewCarta(listType: CartaListType) {
        viewModelScope.launch {
            val newCarta = Carta(
                id = UUID.randomUUID().toString(),
                name = "Nueva carta",
                expansionCode = "SWSH",
                cardNumber = "1/202",
                price = 0.0,
                currency = "USD",
                listTypeRaw = listType.value
            )
            repository.insertCarta(newCarta)
        }
    }

    fun deleteCarta(carta: Carta) {
        viewModelScope.launch {
            repository.deleteCarta(carta)
        }
    }

    suspend fun getCartaById(id: String): CartaWithVariants? {
        return repository.getCartaById(id)
    }
}
