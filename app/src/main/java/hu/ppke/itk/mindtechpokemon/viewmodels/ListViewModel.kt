package hu.ppke.itk.mindtechpokemon.viewmodels

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import hu.ppke.itk.mindtechpokemon.model.PokemonRepository
import hu.ppke.itk.mindtechpokemon.model.PokemonShortEntity

class ListViewModel(
    pokemonRepository: PokemonRepository
) : ViewModel(), NavController.OnDestinationChangedListener {

    private val listing = pokemonRepository.pokemons(viewModelScope.coroutineContext)

    val data = listing.displayList
    val networkState = listing.networkState

    fun callback(position: Int, type: Int) {
        listing.onItemBound(position, type)
    }

    fun refresh() = listing.refresh()
    fun retry() = listing.retry()


    fun onClick(view: View, pokemon: PokemonShortEntity) {

        //TODO: navigate to DetailFragment
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
       // if (navigationQueue.isNotEmpty()) navigation.postValue(navigationQueue.poll())
    }




}