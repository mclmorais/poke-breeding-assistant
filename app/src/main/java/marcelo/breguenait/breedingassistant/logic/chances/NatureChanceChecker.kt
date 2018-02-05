package marcelo.breguenait.breedingassistant.logic.chances

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import javax.inject.Inject


class NatureChanceChecker @Inject constructor() {

    private val CHANCE_PASSING_NATURE_EVERSTONE = 1.0
    private val CHANCE_PASSING_NATURE_RANDOM = 1.0 / 25.0

    fun getNatureChanceMultiplier(relatedPokemon: InternalPokemon, compatiblePokemon: InternalPokemon, targetPokemon: InternalPokemon): Double {
        //If neither pokemon has the nature of the target, the only chance is random (1 in 25), otherwise is guaranteed
        //that the children will have it (if the appropriate parent uses an everstone)
        if (relatedPokemon.natureId == targetPokemon.natureId || compatiblePokemon.natureId == targetPokemon.natureId)
            return CHANCE_PASSING_NATURE_EVERSTONE
        else
            return CHANCE_PASSING_NATURE_RANDOM
    }

    companion object {
        val INCOMPATIBLE_NATURE = -11
    }
}
