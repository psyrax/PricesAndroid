package com.psyrax.pokeprices.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CartaWithVariants(
    @Embedded val carta: Carta,
    @Relation(
        parentColumn = "id",
        entityColumn = "cartaId"
    )
    val variants: List<CartaVariant>
)
