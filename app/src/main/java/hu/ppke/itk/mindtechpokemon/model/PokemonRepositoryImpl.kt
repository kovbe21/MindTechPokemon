package hu.ppke.itk.mindtechpokemon.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import hu.ppke.itk.mindtechpokemon.basic.Listing
import hu.ppke.itk.mindtechpokemon.basic.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import me.sargunvohra.lib.pokekotlin.client.PokeApi
import me.sargunvohra.lib.pokekotlin.model.NamedApiResource
import kotlin.coroutines.CoroutineContext

class PokemonRepositoryImpl(
    private val pokemonDao: PokemonDao,
    private val api: PokeApi
) : PokemonRepository, CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Unconfined

    private val TAG = "PokemonRepositoryImpl"

    private suspend fun load(from: Int, count: Int, reset: Boolean, state: MutableLiveData<NetworkState>): Listing.LoadResult? {

        try {
            val pokemons = api.getPokemonList(from,count)
            Log.v(this@PokemonRepositoryImpl.TAG, "loading from $from-${from+count}")
            val loadedCount = insertIntoDb(pokemons.results, reset)
            state.postValue(NetworkState.loaded(pokemons.results.size))
            return Listing.LoadResult(loadedCount, pokemons.results.size)
        } catch (e: Exception) {
            Log.w(this@PokemonRepositoryImpl.TAG, "load error", e)
            state.postValue(NetworkState.error(e.message))
        }
        return null
    }


    private suspend fun insertIntoDb(
        pokemons: List<NamedApiResource>?,
        delete: Boolean
    ): Int {

        var entities = emptyList<PokemonShortEntity>()
        if (pokemons != null) {
            entities = pokemons.map{ PokemonShortEntity(it)}
        }


        if (delete) {
            pokemonDao.replaceAll(entities)
        } else {
            pokemonDao.insert(entities)
        }
        return entities.size

    }


    override fun pokemons(parentContext: CoroutineContext): Listing<PokemonShortEntity> =
        Listing(
            parentContext,
            pokemonDao.observePokemons(),
            networkLoadCall = ::load
        )




}