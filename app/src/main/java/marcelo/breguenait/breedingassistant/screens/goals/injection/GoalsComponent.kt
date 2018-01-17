package marcelo.breguenait.breedingassistant.screens.goals.injection

import dagger.Component
import marcelo.breguenait.breedingassistant.application.injection.ApplicationComponent
import marcelo.breguenait.breedingassistant.screens.goals.GoalsActivity

@GoalsScope
@Component(dependencies = [ApplicationComponent::class])
interface GoalsComponent {

    fun inject(goalsActivity: GoalsActivity)

}
