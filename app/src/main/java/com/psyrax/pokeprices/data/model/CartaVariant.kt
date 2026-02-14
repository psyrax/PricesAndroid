package com.psyrax.pokeprices.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "carta_variants",
    foreignKeys = [
        ForeignKey(
            entity = Carta::class,
            parentColumns = ["id"],
            childColumns = ["cartaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cartaId"])]
)
data class CartaVariant(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val cartaId: String,
    val condition: String,
    val printing: String,
    val price: Double,
    val lastUpdated: Int
)
