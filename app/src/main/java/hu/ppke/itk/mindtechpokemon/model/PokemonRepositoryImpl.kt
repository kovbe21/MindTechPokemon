package hu.ppke.itk.mindtechpokemon.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.ppke.itk.mindtechpokemon.basic.Listing
import hu.ppke.itk.mindtechpokemon.basic.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.sargunvohra.lib.pokekotlin.client.PokeApi
import me.sargunvohra.lib.pokekotlin.model.NamedApiResource
import kotlin.coroutines.CoroutineContext

class PokemonRepositoryImpl(
    private val shortPokemonDao: ShortPokemonDao,
    private val detailedPokemonDao: DetailedPokemonDao,
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
            shortPokemonDao.replaceAll(entities)
        } else {
            shortPokemonDao.insert(entities)
        }
        return entities.size

    }


    override fun pokemons(parentContext: CoroutineContext): Listing<PokemonShortEntity> =
        Listing(
            parentContext,
            shortPokemonDao.observePokemons(),
            networkLoadCall = ::load
        )

    override fun loadDetailedPokemon(pokeID: Int): Pair<LiveData<PokemonDetailedEntity> , LiveData<NetworkState>> {
        val result = MutableLiveData<NetworkState>().apply {
            value = NetworkState.LOADING
        }

        launch {
            if ( detailedPokemonDao.getPokemon(pokeID) == null) {
                try {
                    updateDetailedPokemon(pokeID)
                    result.postValue(NetworkState.LOADED)
                } catch (e: Exception) {
                    result.postValue(NetworkState.error(e.message))
                    Log.d(TAG, "", e)
                }
            }
        }
        return detailedPokemonDao.observePokemon(pokeID) to result
    }



    @Throws(Exception::class)
    override suspend fun updateDetailedPokemon(id: Int): PokemonDetailedEntity {

        return PokemonDetailedEntity(api.getPokemon(id)).apply {
            detailedPokemonDao.insert(this)
        }
    }

}