package com.psyrax.pokeprices.data.remote

import com.psyrax.pokeprices.data.remote.dto.ExchangeRateResponse
import retrofit2.http.GET

interface ExchangeRateApi {

    @GET("v6/latest/USD")
    suspend fun getUsdRates(): ExchangeRateResponse
}
