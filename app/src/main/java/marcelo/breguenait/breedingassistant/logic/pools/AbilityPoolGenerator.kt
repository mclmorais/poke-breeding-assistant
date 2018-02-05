package marcelo.breguenait.breedingassistant.logic.pools

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.logic.Couple
import marcelo.breguenait.breedingassistant.logic.chances.AbilityChanceChecker
import javax.inject.Inject

class AbilityPoolGenerator @Inject constructor(private val abilityChanceChecker: AbilityChanceChecker,
                                               private val externalRepository: ExternalRepository) {

    fun generate(couple: Couple): IntArray {

        val target = InternalPokemon()
        target.abilitySlot = 2

        val noOfAbilities = externalRepository.getNumberOfAbilities(couple.related.externalId)

        val hiddenAbilityChance = abilityChanceChecker.getAbilityMultiplier(
                couple.related,
                noOfAbilities,
                target)

        if(hiddenAbilityChance > 0) {
            return if(noOfAbilities == 1) intArrayOf(0, 2) else intArrayOf(0, 1, 2)
        }
        else
            return if(noOfAbilities == 1) intArrayOf(0) else intArrayOf(0, 1)
    }

}