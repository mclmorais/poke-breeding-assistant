package marcelo.breguenait.breedingassistant.data.external.datablocks

import android.support.annotation.IntDef

/**
 * Holds information retrieved from external files about an ability.
 */
class ExternalAbility(val id: Int, val name: String) {
    companion object {
        const val FIRST_SLOT = 0L
        const val SECOND_SLOT = 1L
        const val HIDDEN_SLOT = 2L

        @IntDef(FIRST_SLOT, SECOND_SLOT, HIDDEN_SLOT)
        annotation class Slots
    }
}
