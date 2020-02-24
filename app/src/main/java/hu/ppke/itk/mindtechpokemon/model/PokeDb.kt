package hu.ppke.itk.mindtechpokemon.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PokemonShortEntity::class , PokemonDetailedEntity::class],
    version = 1,
    exportSchema = false
)

abstract class PokeDb : RoomDatabase() {
    companion object {
        fun create(context: Context): PokeDb {
            val databaseBuilder =
                Room.inMemoryDatabaseBuilder(context, PokeDb::class.java)

            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun shortPokemon(): ShortPokemonDao
    abstract fun detailedPokemon(): DetailedPokemonDao

}