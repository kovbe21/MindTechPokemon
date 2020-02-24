package hu.ppke.itk.mindtechpokemon.viewmodels

import android.view.View
import androidx.lifecycle.viewModelScope
import hu.ppke.itk.mindtechpokemon.basic.NavViewModel
import hu.ppke.itk.mindtechpokemon.model.PokemonRepository
import hu.ppke.itk.mindtechpokemon.model.PokemonShortEntity
import hu.ppke.itk.mindtechpokemon.view.PokeListFragmentDirections

class ListViewModel(
    pokemonRepository: PokemonRepository
) : NavViewModel() {

    private val listing = pokemonRepository.pokemons(viewModelScope.coroutineContext)

    val data = listing.displayList
    val networkState = listing.networkState

    fun callback(position: Int, type: Int) {
        listing.onItemBound(position, type)
    }

    fun refresh() = listing.refresh()
    fun retry() = listing.retry()


    fun onClick(view: View, pokemon: PokemonShortEntity) {

        navigateTo(PokeListFragmentDirections.actionListFragmentToDetailFragment(pokemon.id))

    }





}