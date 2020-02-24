package hu.ppke.itk.mindtechpokemon.viewmodels

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import hu.ppke.itk.mindtechpokemon.R
import hu.ppke.itk.mindtechpokemon.basic.NetworkState
import hu.ppke.itk.mindtechpokemon.basic.Status
import hu.ppke.itk.mindtechpokemon.model.PokemonDetailedEntity
import hu.ppke.itk.mindtechpokemon.model.PokemonRepository
import kotlinx.coroutines.launch

class DetailedViewModel(
    private val pokemonRepository: PokemonRepository
) : ViewModel(){


    private val mutableMessage = LiveEvent<@StringRes Int>()
    val message get() = mutableMessage as LiveData<Int>

    private val mutableNetworkState = LiveEvent<Status>()
    val networkState get() = mutableNetworkState as LiveData<Status>

    private val mutableLoadResult = LiveEvent<Boolean>()
    val loadResult get() = mutableLoadResult as LiveData<Boolean>


    fun loadPokemon(pokeID: Int): Pair<LiveData<PokemonDetailedEntity>, LiveData<NetworkState>> =
        pokemonRepository.loadDetailedPokemon(pokeID)

    fun updatePokemon(pokeID: Int) = viewModelScope.launch {
        mutableNetworkState.value = Status.RUNNING
        try {
            pokemonRepository.updateDetailedPokemon(pokeID)
            mutableNetworkState.value = Status.SUCCESS
        } catch (e: Exception) {
            Log.e("DetailedViewModel", "failed to update transaction", e)
            mutableNetworkState.value = Status.FAILED
            mutableMessage.value = R.string.network_error
        }
    }

}