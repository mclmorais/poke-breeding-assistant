package marcelo.breguenait.breedingassistant.data.external.datablocks

import androidx.annotation.IntDef

/**
 * Holds information retrieved from external files about an ability.
 */
class ExternalAbility(val id: Int, val name: String) {
    companion object {
        const val FIRST_SLOT = 0
        const val SECOND_SLOT = 1
        const val HIDDEN_SLOT = 2

        @IntDef(FIRST_SLOT, SECOND_SLOT, HIDDEN_SLOT)
        annotation class Slots
    }
}
