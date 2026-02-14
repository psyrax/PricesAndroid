package com.psyrax.pokeprices.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.psyrax.pokeprices.data.model.Carta
import com.psyrax.pokeprices.data.model.CartaVariant
import com.psyrax.pokeprices.data.model.GameSet

@Database(
    entities = [Carta::class, CartaVariant::class, GameSet::class],
    version = 1,
    exportSchema = false
)
abstract class PokePricesDatabase : RoomDatabase() {
    abstract fun cartaDao(): CartaDao
    abstract fun cartaVariantDao(): CartaVariantDao
    abstract fun gameSetDao(): GameSetDao

    companion object {
        @Volatile
        private var INSTANCE: PokePricesDatabase? = null

        fun getDatabase(context: Context): PokePricesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokePricesDatabase::class.java,
                    "pokeprices_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
