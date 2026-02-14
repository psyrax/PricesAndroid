package com.psyrax.pokeprices.data.remote.dto

import com.google.gson.annotations.SerializedName

// === Cards ===

data class CardsResponse(
    val data: List<CardDTO>
)

data class SingleCardResponse(
    val data: CardDTO
)

data class CardDTO(
    val id: String,
    val name: String?,
    val game: String?,
    val number: String?,
    val rarity: String?,
    val tcgplayerId: String?,
    val details: String?,
    val set: String?,
    @SerializedName("set_name") val setName: String?,
    val variants: List<VariantDTO>?,
    val images: ImageDTO?,
    val cardmarket: CardMarketDTO?,
    val tcgplayer: TcgPlayerDTO?
)

data class VariantDTO(
    val condition: String?,
    val printing: String?,
    val price: Double?,
    val lastUpdated: Int?
)

data class ImageDTO(
    val small: String?,
    val large: String?
)

data class CardMarketDTO(
    val prices: CardMarketPrices?
)

data class CardMarketPrices(
    val averageSellPrice: Double?,
    val lowPrice: Double?,
    val trendPrice: Double?
)

data class TcgPlayerDTO(
    val prices: TcgPlayerPrices?
)

data class TcgPlayerPrices(
    val market: MarketPriceContainer?,
    val averageMarketPrice: Double?
)

data class MarketPriceContainer(
    val marketPrice: Double?
)

// === Sets ===

data class SetsResponse(
    val data: List<GameSetDTO>
)

data class GameSetDTO(
    val id: String,
    val name: String,
    @SerializedName("game_id") val gameId: String,
    val game: String,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("cards_count") val cardsCount: Int
)

// === Exchange Rate ===

data class ExchangeRateResponse(
    val rates: Map<String, Double>
)
