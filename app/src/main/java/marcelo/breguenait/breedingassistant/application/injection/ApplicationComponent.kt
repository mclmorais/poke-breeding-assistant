package marcelo.breguenait.breedingassistant.application.injection

import dagger.Component
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.external.injection.ExternalPokemonModule
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons
import marcelo.breguenait.breedingassistant.utils.injection.CachedPokemonIconsModule

/**
 * Created by Marcelo on 17/01/2018.
 */
@ApplicationScope
@Component(modules = [CachedPokemonIconsModule::class, ApplicationModule::class, ExternalPokemonModule::class])
interface ApplicationComponent {

    fun getExternalPokemonsRepository(): ExternalRepository

    fun cachedPokemonIcons(): CachedPokemonIcons

}