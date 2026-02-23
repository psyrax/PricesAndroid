package com.psyrax.pokeprices.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psyrax.pokeprices.PokePricesApp
import com.psyrax.pokeprices.data.model.CartaWithVariants
import com.psyrax.pokeprices.data.model.GameSet
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as PokePricesApp
    private val repository = app.cartaRepository
    val settingsDataStore = app.settingsDataStore
    val sets: Flow<List<GameSet>> = repository.getAllSets()
    val usdToMxnRate: Flow<Double> = settingsDataStore.usdToMxnRate

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _selectedSet = MutableStateFlow<GameSet?>(null)
    val selectedSet: StateFlow<GameSet?> = _selectedSet

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _results = MutableStateFlow<List<CartaWithVariants>>(emptyList())
    val results: StateFlow<List<CartaWithVariants>> = _results

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched

    fun updateSearchText(text: String) { _searchText.value = text }
    fun selectSet(set: GameSet?) { _selectedSet.value = set }

    fun performSearch(apiKey: String) {
        val query = _searchText.value.trim()
        if (query.isEmpty() || apiKey.isEmpty()) return

        viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null
            _results.value = emptyList()
            _hasSearched.value = true

            try {
                val set = _selectedSet.value
                _results.value = if (set != null) {
                    repository.searchCardsByNameAndSet(query, set.id, apiKey)
                } else {
                    repository.searchCards(query, apiKey, pageSize = 30)
                }
            } catch (e: Exception) {
                Log.e("PokePrices", "performSearch error", e)
                _errorMessage.value = "Error al buscar: ${e.message}"
            }
            _isSearching.value = false
        }
    }
}
