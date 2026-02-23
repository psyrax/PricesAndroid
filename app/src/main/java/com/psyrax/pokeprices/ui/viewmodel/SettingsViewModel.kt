package com.psyrax.pokeprices.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psyrax.pokeprices.PokePricesApp
import com.psyrax.pokeprices.data.model.CartaVariant
import com.psyrax.pokeprices.data.model.GameSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as PokePricesApp
    private val repository = app.cartaRepository
    private val currencyRepository = app.currencyRepository
    val settingsDataStore = app.settingsDataStore

    val usdToMxnRate: Flow<Double> = settingsDataStore.usdToMxnRate

    // StateFlow hot: null = DataStore aún no cargó, "" = sin key, "xxx" = key configurada
    val apiKeyState: StateFlow<String?> = settingsDataStore.apiKey
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _isRefreshingSets = MutableStateFlow(false)
    val isRefreshingSets: StateFlow<Boolean> = _isRefreshingSets

    private val _refreshMessage = MutableStateFlow<String?>(null)
    val refreshMessage: StateFlow<String?> = _refreshMessage

    private val _isUpdatingCards = MutableStateFlow(false)
    val isUpdatingCards: StateFlow<Boolean> = _isUpdatingCards

    private val _updateProgress = MutableStateFlow<String?>(null)
    val updateProgress: StateFlow<String?> = _updateProgress

    private val _isUpdatingRate = MutableStateFlow(false)
    val isUpdatingRate: StateFlow<Boolean> = _isUpdatingRate

    private val _rateUpdateMessage = MutableStateFlow<String?>(null)
    val rateUpdateMessage: StateFlow<String?> = _rateUpdateMessage

    private val _cartaCount = MutableStateFlow(0)
    val cartaCount: StateFlow<Int> = _cartaCount

    private val _apiKeyStatus = MutableStateFlow<String?>(null)
    val apiKeyStatus: StateFlow<String?> = _apiKeyStatus

    init {
        viewModelScope.launch {
            repository.getForSaleCartas().combine(repository.getWantToBuyCartas()) { a, b ->
                a.size + b.size
            }.collect { _cartaCount.value = it }
        }

        // Auto-verificar y cargar sets cuando el usuario cambia la API key
        viewModelScope.launch {
            settingsDataStore.apiKey
                .drop(1)                  // omitir el valor guardado al iniciar la app
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .debounce(1500)           // esperar 1.5s después de que el usuario deje de escribir
                .collect { key -> autoVerifyAndRefreshSets(key) }
        }
    }

    private fun autoVerifyAndRefreshSets(key: String) {
        viewModelScope.launch {
            _isRefreshingSets.value = true
            _apiKeyStatus.value = "Verificando..."
            _refreshMessage.value = null
            try {
                val dtos = repository.fetchSets(key)
                repository.saveSets(dtos)
                _apiKeyStatus.value = "✅ API Key válida"
                _refreshMessage.value = "✅ ${dtos.size} sets cargados automáticamente"
            } catch (e: Exception) {
                Log.e("PokePrices", "autoVerifyAndRefreshSets error", e)
                _apiKeyStatus.value = "❌ API Key inválida o sin conexión"
            }
            _isRefreshingSets.value = false
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch { settingsDataStore.saveApiKey(key) }
    }

    fun saveRate(rate: Double) {
        viewModelScope.launch { settingsDataStore.saveUsdToMxnRate(rate) }
    }

    fun refreshSets(apiKey: String) {
        viewModelScope.launch {
            _isRefreshingSets.value = true
            _refreshMessage.value = null
            try {
                val dtos = repository.fetchSets(apiKey)
                repository.saveSets(dtos)
                _refreshMessage.value = "✅ ${dtos.size} sets actualizados"
            } catch (e: Exception) {
                Log.e("PokePrices", "refreshSets error", e)
                _refreshMessage.value = "❌ Error: ${e.message}"
            }
            _isRefreshingSets.value = false
        }
    }

    fun updateExchangeRate() {
        viewModelScope.launch {
            _isUpdatingRate.value = true
            _rateUpdateMessage.value = "Consultando tasa de cambio..."
            try {
                val newRate = currencyRepository.fetchUsdToMxnRate()
                settingsDataStore.saveUsdToMxnRate(newRate)
                _rateUpdateMessage.value = "✅ Tasa actualizada: ${"%.2f".format(newRate)} MXN"
            } catch (e: Exception) {
                Log.e("PokePrices", "updateExchangeRate error", e)
                _rateUpdateMessage.value = "❌ Error: ${e.message}"
            }
            _isUpdatingRate.value = false
        }
    }

    fun updateAllCards(apiKey: String) {
        viewModelScope.launch {
            _isUpdatingCards.value = true
            _updateProgress.value = "Iniciando actualización..."

            var successCount = 0
            var errorCount = 0

            try {
                val allCartas = repository.getAllCartas()
                for ((index, carta) in allCartas.withIndex()) {
                    _updateProgress.value = "Actualizando ${index + 1} de ${allCartas.size}..."
                    try {
                        val fetched = if (!carta.apiCardId.isNullOrEmpty()) {
                            repository.fetchCard(carta.apiCardId, apiKey)
                        } else {
                            repository.searchCardsByNameAndSet(carta.name, carta.expansionCode, apiKey).firstOrNull()
                        }

                        if (fetched != null) {
                            val updated = carta.copy(
                                apiId = fetched.carta.apiId,
                                apiCardId = fetched.carta.apiCardId,
                                name = fetched.carta.name,
                                game = fetched.carta.game,
                                expansionName = fetched.carta.expansionName,
                                cardNumber = fetched.carta.cardNumber,
                                rarity = fetched.carta.rarity,
                                tcgplayerId = fetched.carta.tcgplayerId,
                                details = fetched.carta.details,
                                imageURL = fetched.carta.imageURL,
                                price = fetched.carta.price,
                                currency = fetched.carta.currency
                            )
                            val variants = fetched.variants.map {
                                it.copy(cartaId = carta.id)
                            }
                            repository.updateCarta(updated, variants)
                            successCount++
                        } else {
                            errorCount++
                        }
                    } catch (e: Exception) {
                        errorCount++
                    }
                    delay(500)
                }
                _updateProgress.value = "✅ Actualización completa: $successCount exitosas, $errorCount errores"
            } catch (e: Exception) {
                _updateProgress.value = "❌ Error: ${e.message}"
            }
            _isUpdatingCards.value = false
        }
    }
}
