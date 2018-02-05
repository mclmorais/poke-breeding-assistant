package marcelo.breguenait.breedingassistant.logic.chances

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.logic.BreedingManager
import marcelo.breguenait.breedingassistant.logic.chances.GenderChanceChecker
import marcelo.breguenait.breedingassistant.logic.chances.IvChanceChecker
import marcelo.breguenait.breedingassistant.logic.chances.NatureChanceChecker
import marcelo.breguenait.breedingassistant.logic.chances.AbilityChanceChecker
import javax.inject.Inject

class ChanceCalculator @Inject constructor(private val ivChanceChecker: IvChanceChecker,
                                           private val natureChanceChecker: NatureChanceChecker,
                                           private val abilityChanceChecker: AbilityChanceChecker,
                                           private val genderChanceChecker: GenderChanceChecker){

    fun getTotalChance(first: InternalPokemon, second: InternalPokemon, target: InternalPokemon, @BreedingManager.ChanceFlags
    flags: Int = 0): Double {

        var finalChance: Double

        val ivsChance: Double
        if (flags and BreedingManager.CONSIDER_STRICT_IVS != 0)
            ivsChance = ivChanceChecker.getStrictIvChance(first.IVs, second.IVs, target.IVs)
        else
            ivsChance = ivChanceChecker.getIvChance(first.IVs, second.IVs, target.IVs)

        val natureChance = natureChanceChecker.getNatureChanceMultiplier(first, second, target)

        val abilityChance = abilityChanceChecker.getAbilityMultiplier(first, target)

        finalChance = ivsChance * natureChance * abilityChance

        if (flags and BreedingManager.CONSIDER_GENDER != 0) {

            val genderChance = genderChanceChecker.getGenderChance(target)

            finalChance *= genderChance
        }

        return finalChance
    }

}
