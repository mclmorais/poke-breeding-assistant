package marcelo.breguenait.breedingassistant.application

import android.app.Activity
import android.app.Application
import marcelo.breguenait.breedingassistant.application.injection.ApplicationComponent
import marcelo.breguenait.breedingassistant.application.injection.ApplicationModule
import marcelo.breguenait.breedingassistant.application.injection.DaggerApplicationComponent


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
                .applicationModule(ApplicationModule(app))
                .build()
    }

}
