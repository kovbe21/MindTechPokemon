package hu.ppke.itk.mindtechpokemon.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokeshort: PokemonShortEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<PokemonShortEntity>): List<Long>

    @Query("DELETE FROM pokemon")
    suspend fun deleteAll(): Int

    @Transaction
    suspend fun replaceAll(list: List<PokemonShortEntity>): List<Long> {
        deleteAll()
        return insert(list)
    }


    @Query("SELECT * FROM pokemon ")
    fun observePokemons(): LiveData<List<PokemonShortEntity>>



    @Query("SELECT * FROM pokemon WHERE id = :id")
    fun getPokemonShort(id: Int): LiveData<PokemonShortEntity>
}