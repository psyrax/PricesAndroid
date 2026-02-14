package com.psyrax.pokeprices

import android.app.Application
import com.psyrax.pokeprices.data.local.PokePricesDatabase
import com.psyrax.pokeprices.data.local.SettingsDataStore
import com.psyrax.pokeprices.data.repository.CartaRepository
import com.psyrax.pokeprices.data.repository.CurrencyRepository

class PokePricesApp : Application() {
    val database by lazy { PokePricesDatabase.getDatabase(this) }
    val cartaRepository by lazy {
        CartaRepository(
            cartaDao = database.cartaDao(),
            variantDao = database.cartaVariantDao(),
            gameSetDao = database.gameSetDao()
        )
    }
    val currencyRepository by lazy { CurrencyRepository() }
    val settingsDataStore by lazy { SettingsDataStore(this) }
}
