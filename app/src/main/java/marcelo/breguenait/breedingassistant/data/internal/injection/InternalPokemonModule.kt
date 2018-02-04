package marcelo.breguenait.breedingassistant.data.internal.injection

import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import marcelo.breguenait.breedingassistant.application.injection.ApplicationScope
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemonDataSource
import marcelo.breguenait.breedingassistant.data.internal.InternalRepository

/**
 * Created by Marcelo on 04/02/2018.
 */
@Module
class InternalPokemonModule {

    @Provides
    @ApplicationScope
    fun provideInternalPokemonRepository(dataSource: InternalPokemonDataSource): InternalRepository =
            InternalRepository(dataSource)

    @Provides
    @ApplicationScope
    fun provideInternalPokemonDataSource(sharedPreferences: SharedPreferences, gson: Gson): InternalPokemonDataSource =
        InternalPokemonDataSource(sharedPreferences, gson)

}