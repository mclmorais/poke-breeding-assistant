package marcelo.breguenait.breedingassistant.screens.creation.injection

import dagger.Component
import marcelo.breguenait.breedingassistant.application.injection.ApplicationComponent
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity

/**
 * Created by Marcelo on 22/01/2018.
 */
@CreationScope
@Component(dependencies = [ApplicationComponent::class])
interface CreationComponent {

    fun inject(creationActivity: CreationActivity): CreationActivity
}