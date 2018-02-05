package marcelo.breguenait.breedingassistant.logic

import android.support.annotation.IntDef
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.data.internal.InternalRepository
import marcelo.breguenait.breedingassistant.logic.chances.ChanceCalculator
import marcelo.breguenait.breedingassistant.logic.pools.ChildrenPoolGenerator
import marcelo.breguenait.breedingassistant.utils.Genders.DITTO
import marcelo.breguenait.breedingassistant.utils.Genders.GENDERLESS
import java.util.*
import javax.inject.Inject

class BreedingManager @Inject constructor(private val externalRepository: ExternalRepository,
                                          private val internalRepository: InternalRepository,
                                          private val childrenPoolGenerator: ChildrenPoolGenerator,
                                          private val chanceCalculator: ChanceCalculator,
                                          private val compatibilityChecker: CompatibilityChecker) {


    var directFlag = DIRECT_NONE
        private set


    val directChances: List<CombinationHolder>
        @Throws(CompatibilityChecker.CompatibilityException::class)
        get() {
            val combinations = storedPokemonCombinations
            for (combination in combinations) {
                val chance = chanceCalculator.getTotalChance(combination.couple.related, combination.couple.compatible!!, internalRepository.currentGoal!!)
                combination.setCombinedChildrenChance(chance)
            }
            setDirectFlag(combinations)


            return combinations.filter { java.lang.Double.compare(it.chance, 0.0) > 0 }
        }

    val improvementChances: List<CombinationHolder>
        @Throws(CompatibilityChecker.CompatibilityException::class)
        get() {
            val combinations = storedPokemonCombinations
            for (combination in combinations) {

                val pool = childrenPoolGenerator.generate(combination.couple, internalRepository.currentGoal!!)//generate(combination.couple)

                val childrenPool = pool.first
                val multiplier = pool.second

                var totalChance = 0.0
                for (child in childrenPool) {
                    val chance = chanceCalculator.getTotalChance(combination.couple.related, combination.couple.compatible!!, child.childPokemon, CONSIDER_STRICT_IVS)
                    child.setChance(chance)
                    addBestCoupleToChild(child)
                    totalChance += chance
                }

                combination.setChildren(childrenPool, multiplier)
            }

            return combinations
        }

    fun sortChances(list: List<CombinationHolder>): List<CombinationHolder> {
        return list.sortedByDescending { it.chance }
    }

    private val storedPokemonCombinations: List<CombinationHolder>
        @Throws(CompatibilityChecker.CompatibilityException::class)
        get() {

            val storedPokemons = internalRepository.getStoredPokemons()
            val goal = internalRepository.currentGoal

            val relatedPokemons = compatibilityChecker.getRelatedStoredPokemons(storedPokemons, goal!!)
            val compatiblePokemons = compatibilityChecker.getCompatibleStoredPokemon(storedPokemons, goal)

            val storedPokemonCombinations = ArrayList<CombinationHolder>(
                    relatedPokemons.size * compatiblePokemons.size)
            for (related in relatedPokemons) {
                compatiblePokemons
                        .map { Couple(related, it) }
                        .filter { compatibilityChecker.checkGenderCompatibility(it) }
                        .mapTo(storedPokemonCombinations, ::CombinationHolder)
            }

            return storedPokemonCombinations
        }

    private fun setDirectFlag(combinations: List<CombinationHolder>) {

        val goal = internalRepository.currentGoal
        val goalGenderRestriction = externalRepository.getGenderRestriction(goal!!.externalId)!!

        if (combinations.isEmpty()) {
            if (goalGenderRestriction == GENDERLESS)
                directFlag = DIRECT_NO_PARENTS_GENDERLESS
            else
                directFlag = DIRECT_NO_PARENTS_GENDERED
            return
        } else if (goal.abilitySlot == 2) {
            var hasHiddenAbility = false

            for (combination in combinations) {
                val related = combination.couple.related
                val compatible = combination.couple.compatible
                if (related.abilitySlot == 2)
                //TODO: change 2 to IntDef
                    hasHiddenAbility = true
                else if (compatible!!.gender != DITTO && related.abilitySlot == 2)
                    hasHiddenAbility = true
            }

            if (!hasHiddenAbility) {
                directFlag = DIRECT_NO_HIDDEN_ABILITY
                return
            }
        }

        directFlag =
                if (combinations.any { java.lang.Double.compare(it.chance, 0.0) > 0 })
                    DIRECT_OK
                else
                    DIRECT_IVS_TOO_LOW


    }

    private fun addBestCoupleToChild(child: Child): Boolean {

        var bestChance = 0.0

        var bestStoredPokemon: InternalPokemon? = null

        for (storedPokemon in compatibilityChecker.getCompatibleStoredPokemon(internalRepository.getStoredPokemons(), internalRepository.currentGoal!!)) {
            val chance = chanceCalculator.getTotalChance(child.childPokemon, storedPokemon, internalRepository.currentGoal ?: return false)

            if (java.lang.Double.compare(chance, bestChance) > 0) {
                bestChance = chance
                bestStoredPokemon = storedPokemon
            }
        }

        if (bestChance > 0) {
            child.setCombination(bestStoredPokemon!!, bestChance)
        }

        return bestStoredPokemon != null


    }

    @IntDef(flag = true, value = *longArrayOf(CONSIDER_STRICT_IVS.toLong(),
                                              CONSIDER_STRICT_NATURE.toLong(),
                                              CONSIDER_ABILITY.toLong(),
                                              CONSIDER_GENDER.toLong()))
    internal annotation class ChanceFlags

    companion object {

        const val DIRECT_NONE = -1
        const val DIRECT_OK = 0
        const val DIRECT_NO_PARENTS_GENDERED = 1
        const val DIRECT_NO_PARENTS_GENDERLESS = 2
        const val DIRECT_NO_HIDDEN_ABILITY = 3
        const val DIRECT_IVS_TOO_LOW = 4

        const internal val CONSIDER_STRICT_NATURE = 1
        const internal val CONSIDER_ABILITY = 1 shl 1
        const internal val CONSIDER_GENDER = 1 shl 2
        const internal val CONSIDER_STRICT_IVS = 1 shl 3
    }

}

