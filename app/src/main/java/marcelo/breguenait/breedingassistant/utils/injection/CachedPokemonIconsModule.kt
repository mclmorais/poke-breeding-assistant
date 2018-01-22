package marcelo.breguenait.breedingassistant.utils.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import marcelo.breguenait.breedingassistant.application.injection.ApplicationScope
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons


/**
 * Created by Marcelo on 22/01/2018.
 */
@Module
class CachedPokemonIconsModule(val context: Context) {

    @Provides
    @ApplicationScope
    fun provideCachedIcons(r: ExternalRepository, c: Context) = CachedPokemonIcons(r, c)

}