package hu.ppke.itk.mindtechpokemon

import android.view.View
import hu.ppke.itk.mindtechpokemon.basic.CommonPagedAdapter
import hu.ppke.itk.mindtechpokemon.model.PokemonShortEntity
import kotlinx.android.synthetic.main.item_list_pokemon.view.*


class PokeListAdapter(
    listener: (View, PokemonShortEntity) -> Unit,
    retry: () -> Unit,
    callback: (Int, Int) -> Unit,
    config: AdapterConfig = AdapterConfig()
) : CommonPagedAdapter<PokemonShortEntity>(
    R.layout.item_list_pokemon,
    { view, pokemonShortEntity -> bind(view, pokemonShortEntity, listener) },
    retry,
    callback,
    config
) {
    companion object {
        private fun bind(
            view: View,
            item: PokemonShortEntity?,
            listener: (View, PokemonShortEntity) -> Unit
        ) {


            if (item != null) {

                view.setOnClickListener { listener(it, item) }
                view.item_list_pokemon_name.text = item.name
                view.item_list_pokemon_category.text = item.category



            }
        }
    }
}