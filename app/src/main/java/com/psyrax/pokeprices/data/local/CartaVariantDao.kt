package com.psyrax.pokeprices.data.local

import androidx.room.*
import com.psyrax.pokeprices.data.model.CartaVariant

@Dao
interface CartaVariantDao {

    @Query("SELECT * FROM carta_variants WHERE cartaId = :cartaId")
    suspend fun getVariantsForCarta(cartaId: String): List<CartaVariant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: CartaVariant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(variants: List<CartaVariant>)

    @Delete
    suspend fun deleteVariant(variant: CartaVariant)

    @Query("DELETE FROM carta_variants WHERE cartaId = :cartaId")
    suspend fun deleteVariantsForCarta(cartaId: String)
}
