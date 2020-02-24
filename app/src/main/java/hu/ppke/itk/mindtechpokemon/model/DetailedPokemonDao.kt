package hu.ppke.itk.mindtechpokemon.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DetailedPokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detailedpokemon: PokemonDetailedEntity): Long

    @Query("DELETE FROM detailedpokemon")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM detailedpokemon WHERE id= :id")
    suspend fun getPokemon(id: Int): PokemonDetailedEntity?

    @Query("SELECT * FROM detailedpokemon WHERE id = :id")
    fun observePokemon(id: Int): LiveData<PokemonDetailedEntity>
}