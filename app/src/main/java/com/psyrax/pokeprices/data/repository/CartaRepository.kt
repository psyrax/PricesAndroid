package com.psyrax.pokeprices.data.repository

import com.psyrax.pokeprices.data.local.CartaDao
import com.psyrax.pokeprices.data.local.CartaVariantDao
import com.psyrax.pokeprices.data.local.GameSetDao
import com.psyrax.pokeprices.data.model.*
import com.psyrax.pokeprices.data.remote.RetrofitClient
import com.psyrax.pokeprices.data.remote.dto.CardDTO
import com.psyrax.pokeprices.data.remote.dto.GameSetDTO
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CartaRepository(
    private val cartaDao: CartaDao,
    private val variantDao: CartaVariantDao,
    private val gameSetDao: GameSetDao
) {
    private val api = RetrofitClient.justTcgApi

    // === Local DB operations ===

    fun getForSaleCartas(): Flow<List<CartaWithVariants>> = cartaDao.getForSaleCartas()
    fun getWantToBuyCartas(): Flow<List<CartaWithVariants>> = cartaDao.getWantToBuyCartas()
    fun getAllSets(): Flow<List<GameSet>> = gameSetDao.getAllSets()

    suspend fun getCartaById(id: String): CartaWithVariants? = cartaDao.getCartaById(id)
    suspend fun getCartaByTagId(tagId: String): CartaWithVariants? = cartaDao.getCartaByTagId(tagId)

    suspend fun insertCarta(carta: Carta, variants: List<CartaVariant> = emptyList()) {
        cartaDao.insertCarta(carta)
        if (variants.isNotEmpty()) {
            variantDao.insertVariants(variants)
        }
    }

    suspend fun updateCarta(carta: Carta, variants: List<CartaVariant> = emptyList()) {
        cartaDao.updateCarta(carta)
        variantDao.deleteVariantsForCarta(carta.id)
        if (variants.isNotEmpty()) {
            variantDao.insertVariants(variants)
        }
    }

    suspend fun deleteCarta(carta: Carta) {
        cartaDao.deleteCarta(carta)
    }

    // === API operations ===

    suspend fun searchCards(query: String, apiKey: String, pageSize: Int = 20): List<CartaWithVariants> {
        val response = api.searchCards(query = query, pageSize = pageSize, apiKey = apiKey)
        return response.data.map { mapDtoToCartaWithVariants(it) }
    }

    suspend fun searchCardsByNameAndSet(cardName: String, setId: String, apiKey: String): List<CartaWithVariants> {
        val response = api.searchCardsByNameAndSet(query = cardName, setId = setId, apiKey = apiKey)
        return response.data.map { mapDtoToCartaWithVariants(it) }
    }

    suspend fun fetchCard(apiId: String, apiKey: String): CartaWithVariants? {
        return try {
            val response = api.getCard(cardId = apiId, apiKey = apiKey)
            mapDtoToCartaWithVariants(response.data)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchSets(apiKey: String, game: String = "pokemon"): List<GameSetDTO> {
        val response = api.getSets(game = game, apiKey = apiKey)
        return response.data
    }

    suspend fun saveSets(dtos: List<GameSetDTO>) {
        val sets = dtos.map { dto ->
            GameSet(
                id = dto.id,
                name = dto.name,
                gameId = dto.gameId,
                game = dto.game,
                releaseDate = dto.releaseDate,
                cardsCount = dto.cardsCount
            )
        }
        gameSetDao.insertSets(sets)
    }

    suspend fun getAllCartas(): List<Carta> = cartaDao.getAllCartas()

    // === Mapping ===

    private fun mapDtoToCartaWithVariants(dto: CardDTO): CartaWithVariants {
        val cartaId = UUID.randomUUID().toString()
        var price: Double? = null

        // JustTCG: primer variante
        dto.variants?.firstOrNull()?.price?.let { price = it }

        // Fallback: cardmarket
        if (price == null) {
            dto.cardmarket?.prices?.let { cp ->
                price = cp.averageSellPrice ?: cp.trendPrice ?: cp.lowPrice
            }
        }

        // Fallback: tcgplayer
        if (price == null) {
            dto.tcgplayer?.prices?.let { tp ->
                price = tp.market?.marketPrice ?: tp.averageMarketPrice
            }
        }

        val carta = Carta(
            id = cartaId,
            apiId = dto.id,
            apiCardId = dto.id,
            name = dto.name ?: "",
            game = dto.game,
            expansionCode = dto.set ?: "",
            expansionName = dto.setName,
            cardNumber = dto.number ?: "",
            rarity = dto.rarity,
            tcgplayerId = dto.tcgplayerId,
            details = dto.details,
            imageURL = dto.images?.small,
            price = price,
            currency = "USD"
        )

        val variants = dto.variants?.mapNotNull { v ->
            if (v.condition != null && v.printing != null && v.price != null && v.lastUpdated != null) {
                CartaVariant(
                    cartaId = cartaId,
                    condition = v.condition,
                    printing = v.printing,
                    price = v.price,
                    lastUpdated = v.lastUpdated
                )
            } else null
        } ?: emptyList()

        return CartaWithVariants(carta = carta, variants = variants)
    }
}
