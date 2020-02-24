package hu.ppke.itk.mindtechpokemon.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import hu.ppke.itk.mindtechpokemon.basic.ModelBase
import me.sargunvohra.lib.pokekotlin.model.Pokemon

@Entity(
    tableName = "detailedpokemon",
    indices = [Index(value = ["id"])]
)
@TypeConverters(StringListConverter::class)
data class PokemonDetailedEntity (
    @PrimaryKey override val id: Int,
    val name: String?,
    val height: Int?,
    val weight: Int?,
    var notHiddenAbilities: List<String>,
    val ImgSrcLink: String?
) : ModelBase {


    constructor(pokemon: Pokemon) :this(
        pokemon.id,
        pokemon.name,
        pokemon.height,
        pokemon.weight,
        emptyList(),
        pokemon.sprites.frontDefault
    ){
        val list = mutableListOf<String>()
        pokemon.abilities.onEach {
            if(!it.isHidden){
                list.add(it.ability.name)
            }
        }
        notHiddenAbilities=list
    }


}