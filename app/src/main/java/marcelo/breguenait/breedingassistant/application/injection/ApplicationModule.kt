package marcelo.breguenait.breedingassistant.application.injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Marcelo on 17/01/2018.
 */
@ApplicationScope
@Module
class ApplicationModule(private val app: Application, private val sharedPreferences: SharedPreferences) {

    @Provides
    @ApplicationScope
    fun provideContext(): Context = app

    @Provides
    @ApplicationScope
    fun provideSharedPreferences(): SharedPreferences = sharedPreferences
}