package marcelo.breguenait.breedingassistant.application

import android.app.Activity
import android.app.Application
import android.preference.PreferenceManager
import marcelo.breguenait.breedingassistant.application.injection.ApplicationComponent
import marcelo.breguenait.breedingassistant.application.injection.ApplicationModule
import marcelo.breguenait.breedingassistant.application.injection.DaggerApplicationComponent
import marcelo.breguenait.breedingassistant.data.external.injection.ExternalPokemonModule
import marcelo.breguenait.breedingassistant.data.internal.injection.InternalPokemonModule
import marcelo.breguenait.breedingassistant.utils.injection.CachedPokemonIconsModule
import marcelo.breguenait.breedingassistant.utils.injection.GsonModule


class BreedingAssistantApplication : Application() {
    lateinit var component: ApplicationComponent

    companion object {
        fun get(activity: Activity): BreedingAssistantApplication =
            activity.application as BreedingAssistantApplication
    }

    override fun onCreate() {
        super.onCreate()
        initDagger(this)
    }

    private fun initDagger(app: BreedingAssistantApplication) {
        component = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(app, PreferenceManager
                .getDefaultSharedPreferences(this)))
            .internalPokemonModule(InternalPokemonModule())
            .externalPokemonModule(ExternalPokemonModule(assets))
            .cachedPokemonIconsModule(CachedPokemonIconsModule(this))
            .gsonModule(GsonModule())
            .build()
    }

}
