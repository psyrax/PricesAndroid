package com.psyrax.pokeprices.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sets")
data class GameSet(
    @PrimaryKey val id: String,
    val name: String,
    val gameId: String,
    val game: String,
    val releaseDate: String?,
    val cardsCount: Int
)
