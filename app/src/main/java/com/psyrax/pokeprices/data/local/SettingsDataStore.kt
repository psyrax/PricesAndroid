package com.psyrax.pokeprices.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pokeprices_settings")

object PreferencesKeys {
    val API_KEY = stringPreferencesKey("justTcgApiKey")
    val USD_TO_MXN_RATE = doublePreferencesKey("usdToMxnRate")
}

class SettingsDataStore(private val context: Context) {

    val apiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.API_KEY] ?: ""
    }

    val usdToMxnRate: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.USD_TO_MXN_RATE] ?: 18.5
    }

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.API_KEY] = key
        }
    }

    suspend fun saveUsdToMxnRate(rate: Double) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.USD_TO_MXN_RATE] = rate
        }
    }
}
