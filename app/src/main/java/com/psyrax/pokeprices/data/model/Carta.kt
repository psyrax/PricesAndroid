package com.psyrax.pokeprices.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class CartaListType(val value: String) {
    FOR_SALE("forSale"),
    WANT_TO_BUY("wantToBuy");

    companion object {
        fun fromValue(value: String): CartaListType =
            entries.find { it.value == value } ?: FOR_SALE
    }
}

@Entity(tableName = "cartas")
data class Carta(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val listTypeRaw: String = CartaListType.FOR_SALE.value,
    val apiId: String? = null,
    val apiCardId: String? = null,
    val name: String,
    val game: String? = null,
    val expansionCode: String,
    val expansionName: String? = null,
    val cardNumber: String,
    val rarity: String? = null,
    val tcgplayerId: String? = null,
    val details: String? = null,
    val imageURL: String? = null,
    val price: Double? = null,
    val currency: String? = "USD",
    val tagId: String? = null
) {
    val listType: CartaListType
        get() = CartaListType.fromValue(listTypeRaw)
}
