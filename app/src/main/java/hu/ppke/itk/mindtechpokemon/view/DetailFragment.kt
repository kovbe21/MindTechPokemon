package hu.ppke.itk.mindtechpokemon.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import hu.ppke.itk.mindtechpokemon.R
import hu.ppke.itk.mindtechpokemon.basic.Status
import hu.ppke.itk.mindtechpokemon.extensions.showSnack
import hu.ppke.itk.mindtechpokemon.model.PokemonDetailedEntity
import hu.ppke.itk.mindtechpokemon.viewmodels.DetailedViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.android.viewmodel.ext.android.viewModel

class DetailFragment : Fragment(R.layout.fragment_detail){

    private val model: DetailedViewModel by viewModel()
    private val args: DetailFragmentArgs by navArgs()

    private val TAG = "DetailedViewModel"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        model.message.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                showSnack(it)
            }
        })



            model.loadPokemon(args.pokeId).apply {
                first.observe(viewLifecycleOwner, Observer {
                    it?.let { Pokemon ->

                        fragment_detail_progress_bar.visibility=View.GONE
                        setLayout(Pokemon)
                    }
                })

                second.observe(viewLifecycleOwner, Observer {
                    if (it.status == Status.FAILED) {
                        showSnack(R.string.loading_error)
                    }
                })

            }

    }

    private fun setLayout(pokemon: PokemonDetailedEntity){

        fragment_detail_pokemon_name.text=pokemon.name
        fragment_detail_height_weight.text= getString(R.string.fragment_detail_height_weight_string,pokemon.height,pokemon.weight)


        var abilities = "This pokemon has the following abilities: \n"
        pokemon.notHiddenAbilities.forEach {
            abilities+="-"+it+"\n"
        }
        fragment_detail_abilites.text=abilities


        Glide.with(this).load(pokemon.ImgSrcLink).into(fragment_detail_front_default_image)

    }

}