package marcelo.breguenait.breedingassistant.data.external.injection

import android.content.Context
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import marcelo.breguenait.breedingassistant.application.injection.ApplicationScope
import marcelo.breguenait.breedingassistant.data.external.ExternalPokemonDataSource
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons

/**
 * Created by Marcelo on 22/01/2018.
 */

@Module
class ExternalPokemonModule(val assetManager: AssetManager) {


    @Provides
    @ApplicationScope
    fun provideAssetManager(): AssetManager {
        return assetManager
    }

    @Provides
    @ApplicationScope
    fun ProvideExternalPokemonRepository(dataSource: ExternalPokemonDataSource): ExternalRepository =
        ExternalRepository(dataSource)

    @Provides
    @ApplicationScope
    fun provideExternalPokemonDataSource(): ExternalPokemonDataSource = ExternalPokemonDataSource(assetManager)

}