package marcelo.breguenait.breedingassistant.application

import android.app.Activity
import android.app.Application
import marcelo.breguenait.breedingassistant.dagger.AppComponent
import marcelo.breguenait.breedingassistant.dagger.AppModule
import marcelo.breguenait.breedingassistant.dagger.DaggerAppComponent


class BreedingAssistantApplication : Application() {
    lateinit var component: AppComponent

    companion object {
        fun get(activity: Activity): BreedingAssistantApplication =
                activity.application as BreedingAssistantApplication
    }

    override fun onCreate() {
        super.onCreate()
        initDagger(this)
    }

    private fun initDagger(app: BreedingAssistantApplication) {
        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(app))
                .build()
    }

}
