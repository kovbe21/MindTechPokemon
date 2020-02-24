package hu.ppke.itk.mindtechpokemon.model

import androidx.lifecycle.LiveData
import hu.ppke.itk.mindtechpokemon.basic.Listing
import hu.ppke.itk.mindtechpokemon.basic.NetworkState
import kotlin.coroutines.CoroutineContext

interface PokemonRepository {
    fun pokemons(parentContext: CoroutineContext): Listing<PokemonShortEntity>

    fun loadDetailedPokemon(pokeID: Int): Pair<LiveData<PokemonDetailedEntity>, LiveData<NetworkState>>

    @Throws( Exception::class )
    suspend fun updateDetailedPokemon(id: Int): PokemonDetailedEntity


}