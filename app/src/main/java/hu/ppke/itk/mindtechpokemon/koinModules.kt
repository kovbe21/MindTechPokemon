package hu.ppke.itk.mindtechpokemon

import androidx.paging.PagedList
import androidx.preference.PreferenceManager
import hu.ppke.itk.mindtechpokemon.model.PokeDb
import hu.ppke.itk.mindtechpokemon.model.PokemonRepository
import hu.ppke.itk.mindtechpokemon.model.PokemonRepositoryImpl
import hu.ppke.itk.mindtechpokemon.viewmodels.ListViewModel
import me.sargunvohra.lib.pokekotlin.client.PokeApi
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val uiModule = module {
    viewModel { ListViewModel(get()) }
}

val restModule = module {
    // REST & JSON
    single<PokeApi>{ PokeApiClient() }

}


val dataModule = module {


    single<PokemonRepository> { PokemonRepositoryImpl(get(),get()) }


    factory {
        PagedList.Config.Builder()
            .setPageSize(Constants.PAGE_SIZE)
            .setInitialLoadSizeHint(Constants.PAGE_SIZE_FIRST)
            .setPrefetchDistance(Constants.PAGE_PREFETCH)
            .setMaxSize(1000)
            .build()
    }

    single { PokeDb.create(get()) }
    factory { get<PokeDb>().pokemon() }
}
