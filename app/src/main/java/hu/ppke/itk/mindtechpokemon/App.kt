package hu.ppke.itk.mindtechpokemon

import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import android.os.StrictMode
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {


    override fun onCreate() {
        super.onCreate()

        // init KOIN dependency injection
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                   restModule,
                    uiModule,
                    dataModule

                )
            )
        }


    }



}