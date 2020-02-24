package hu.ppke.itk.mindtechpokemon.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import hu.ppke.itk.mindtechpokemon.basic.ModelBase
import me.sargunvohra.lib.pokekotlin.model.NamedApiResource

@Entity(
    tableName = "pokemon",
    indices = [Index(value = ["id"])]
)
data class PokemonShortEntity (
    val name: String?,
    val category: String?,
    @PrimaryKey override val id: Int
) : ModelBase {


    constructor(pokemon: NamedApiResource) : this(
        pokemon.name,
        pokemon.category,
        pokemon.id
    )



}