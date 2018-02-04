package marcelo.breguenait.breedingassistant.utils.injection

import com.google.gson.Gson

import dagger.Module
import dagger.Provides

/**
 * Created by Marcelo on 10/02/2017.
 */

@Module
class GsonModule {

    @Provides
    fun provideGson() = Gson()
}
