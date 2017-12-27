package marcelo.breguenait.breedingassistant.application

import android.app.Activity
import android.app.Application
import marcelo.breguenait.breedingassistant.dagger.AppComponent
import marcelo.breguenait.breedingassistant.dagger.AppModule
import marcelo.breguenait.breedingassistant.dagger.DaggerAppComponent


class BreedingAssistantApplication : Application() {
    lateinit var component: AppComponent

    companion object {
        fun get(activity: Activity): BreedingAssistantApplication {
            return activity.application as BreedingAssistantApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        initDagger(this)
    }

    fun initDagger(app: BreedingAssistantApplication) {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(app))
                .build()
    }
}
