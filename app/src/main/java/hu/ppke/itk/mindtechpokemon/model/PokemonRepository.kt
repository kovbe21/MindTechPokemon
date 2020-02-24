package hu.ppke.itk.mindtechpokemon.model

import hu.ppke.itk.mindtechpokemon.basic.Listing
import kotlin.coroutines.CoroutineContext

interface PokemonRepository {
    fun pokemons(parentContext: CoroutineContext): Listing<PokemonShortEntity>

    //fun getPokemon(Id: Int): LiveData<Pokemon>
}