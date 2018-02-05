package marcelo.breguenait.breedingassistant.screens.assistant.injection

import dagger.Component
import marcelo.breguenait.breedingassistant.application.injection.ApplicationComponent
import marcelo.breguenait.breedingassistant.screens.assistant.AssistantActivity

@AssistantScope
@Component(dependencies = [(ApplicationComponent::class)])
interface AssistantComponent {
    fun inject(assistantActivity: AssistantActivity)
}
