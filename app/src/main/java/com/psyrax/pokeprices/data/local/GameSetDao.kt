package com.psyrax.pokeprices.data.local

import androidx.room.*
import com.psyrax.pokeprices.data.model.GameSet
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSetDao {

    @Query("SELECT * FROM game_sets ORDER BY releaseDate DESC")
    fun getAllSets(): Flow<List<GameSet>>

    @Query("SELECT * FROM game_sets ORDER BY releaseDate DESC")
    suspend fun getAllSetsList(): List<GameSet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: GameSet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<GameSet>)

    @Query("DELETE FROM game_sets")
    suspend fun deleteAllSets()
}
