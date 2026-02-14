package com.psyrax.pokeprices.data.repository

import com.psyrax.pokeprices.data.remote.RetrofitClient

class CurrencyRepository {
    private val api = RetrofitClient.exchangeRateApi

    suspend fun fetchUsdToMxnRate(): Double {
        val response = api.getUsdRates()
        return response.rates["MXN"] ?: throw Exception("MXN rate not found")
    }
}
