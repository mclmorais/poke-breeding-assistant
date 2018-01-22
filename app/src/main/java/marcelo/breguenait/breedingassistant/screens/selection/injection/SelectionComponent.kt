package marcelo.breguenait.breedingassistant.screens.selection.injection

import dagger.Component
import marcelo.breguenait.breedingassistant.application.injection.ApplicationComponent

/**
 * Created by Marcelo on 22/01/2018.
 */
@SelectionScope
@Component(dependencies = [ApplicationComponent::class])
interface SelectionComponent {
}