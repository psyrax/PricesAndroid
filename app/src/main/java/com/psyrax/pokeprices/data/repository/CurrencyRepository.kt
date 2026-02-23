package com.psyrax.pokeprices.data.repository

import com.psyrax.pokeprices.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepository {
    private val api = RetrofitClient.exchangeRateApi

    suspend fun fetchUsdToMxnRate(): Double = withContext(Dispatchers.IO) {
        val response = api.getUsdRates().execute().body()
            ?: throw Exception("Empty response from exchange rate API")
        response.rates["MXN"] ?: throw Exception("MXN rate not found")
    }
}
