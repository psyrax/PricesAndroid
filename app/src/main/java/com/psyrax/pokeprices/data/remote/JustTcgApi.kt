package com.psyrax.pokeprices.data.remote

import com.psyrax.pokeprices.data.remote.dto.CardsResponse
import com.psyrax.pokeprices.data.remote.dto.SetsResponse
import com.psyrax.pokeprices.data.remote.dto.SingleCardResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface JustTcgApi {

    @GET("v1/cards")
    fun searchCards(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("x-api-key") apiKey: String
    ): Call<CardsResponse>

    @GET("v1/cards")
    fun searchCardsByNameAndSet(
        @Query("q") query: String,
        @Query("set") setId: String,
        @Header("x-api-key") apiKey: String
    ): Call<CardsResponse>

    @GET("v1/cards/{id}")
    fun getCard(
        @Path("id") cardId: String,
        @Header("x-api-key") apiKey: String
    ): Call<SingleCardResponse>

    @GET("v1/sets")
    fun getSets(
        @Query("game") game: String,
        @Query("orderBy") orderBy: String,
        @Query("order") order: String,
        @Header("x-api-key") apiKey: String
    ): Call<SetsResponse>
}
