package marcelo.breguenait.breedingassistant.screens.goals.injection

import dagger.Component
import marcelo.breguenait.breedingassistant.dagger.AppComponent
import marcelo.breguenait.breedingassistant.screens.goals.GoalsActivity

@GoalsScope
@Component(dependencies = [AppComponent::class])
interface GoalsComponent {

    fun inject(goalsActivity: GoalsActivity)

}
