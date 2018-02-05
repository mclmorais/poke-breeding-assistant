package marcelo.breguenait.breedingassistant.logic.pools

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.logic.chances.NatureChanceChecker
import javax.inject.Inject

class NaturePoolGenerator @Inject constructor(){

    fun generate(goal : InternalPokemon): IntArray {
        return intArrayOf(goal.natureId, NatureChanceChecker.INCOMPATIBLE_NATURE)
    }

}