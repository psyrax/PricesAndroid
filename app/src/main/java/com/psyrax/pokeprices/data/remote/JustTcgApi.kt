package com.psyrax.pokeprices.data.remote

import com.psyrax.pokeprices.data.remote.dto.CardsResponse
import com.psyrax.pokeprices.data.remote.dto.SetsResponse
import com.psyrax.pokeprices.data.remote.dto.SingleCardResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface JustTcgApi {

    @GET("v1/cards")
    suspend fun searchCards(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Header("x-api-key") apiKey: String
    ): CardsResponse

    @GET("v1/cards")
    suspend fun searchCardsByNameAndSet(
        @Query("q") query: String,
        @Query("set") setId: String,
        @Header("x-api-key") apiKey: String
    ): CardsResponse

    @GET("v1/cards/{id}")
    suspend fun getCard(
        @Path("id") cardId: String,
        @Header("x-api-key") apiKey: String
    ): SingleCardResponse

    @GET("v1/sets")
    suspend fun getSets(
        @Query("game") game: String = "pokemon",
        @Query("orderBy") orderBy: String = "release_date",
        @Query("order") order: String = "desc",
        @Header("x-api-key") apiKey: String
    ): SetsResponse
}
