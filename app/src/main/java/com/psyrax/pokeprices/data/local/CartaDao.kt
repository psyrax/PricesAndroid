package com.psyrax.pokeprices.data.local

import androidx.room.*
import com.psyrax.pokeprices.data.model.Carta
import com.psyrax.pokeprices.data.model.CartaWithVariants
import kotlinx.coroutines.flow.Flow

@Dao
interface CartaDao {

    @Transaction
    @Query("SELECT * FROM cartas ORDER BY name ASC")
    fun getAllCartasWithVariants(): Flow<List<CartaWithVariants>>

    @Transaction
    @Query("SELECT * FROM cartas WHERE listTypeRaw = :listType ORDER BY name ASC")
    fun getCartasByListType(listType: String): Flow<List<CartaWithVariants>>

    @Transaction
    @Query("SELECT * FROM cartas WHERE listTypeRaw = 'forSale' ORDER BY CASE WHEN tagId IS NULL THEN 1 ELSE 0 END, CAST(tagId AS INTEGER), name ASC")
    fun getForSaleCartas(): Flow<List<CartaWithVariants>>

    @Transaction
    @Query("SELECT * FROM cartas WHERE listTypeRaw = 'wantToBuy' ORDER BY name ASC")
    fun getWantToBuyCartas(): Flow<List<CartaWithVariants>>

    @Transaction
    @Query("SELECT * FROM cartas WHERE id = :id")
    suspend fun getCartaById(id: String): CartaWithVariants?

    @Transaction
    @Query("SELECT * FROM cartas WHERE tagId = :tagId LIMIT 1")
    suspend fun getCartaByTagId(tagId: String): CartaWithVariants?

    @Query("SELECT * FROM cartas")
    suspend fun getAllCartas(): List<Carta>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarta(carta: Carta)

    @Update
    suspend fun updateCarta(carta: Carta)

    @Delete
    suspend fun deleteCarta(carta: Carta)

    @Query("DELETE FROM cartas WHERE id = :id")
    suspend fun deleteCartaById(id: String)
}
