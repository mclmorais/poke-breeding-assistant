package marcelo.breguenait.breedingassistant.application.injection

import dagger.Component
import javax.inject.Singleton

/**
 * Created by Marcelo on 17/01/2018.
 */
@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
}