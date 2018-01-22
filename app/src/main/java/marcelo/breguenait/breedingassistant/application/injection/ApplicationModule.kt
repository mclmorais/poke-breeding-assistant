package marcelo.breguenait.breedingassistant.application.injection

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Marcelo on 17/01/2018.
 */
@ApplicationScope
@Module
class ApplicationModule(private val app: Application) {

    @Provides
    @ApplicationScope
    fun provideContext(): Context = app
}