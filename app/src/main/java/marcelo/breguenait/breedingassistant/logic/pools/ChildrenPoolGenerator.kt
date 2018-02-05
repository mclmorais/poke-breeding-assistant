package marcelo.breguenait.breedingassistant.logic.pools

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.logic.Child
import marcelo.breguenait.breedingassistant.logic.Couple
import marcelo.breguenait.breedingassistant.logic.chances.AbilityChanceChecker
import marcelo.breguenait.breedingassistant.logic.chances.GenderChanceChecker
import marcelo.breguenait.breedingassistant.logic.chances.IvChanceChecker
import marcelo.breguenait.breedingassistant.logic.chances.NatureChanceChecker
import javax.inject.Inject

class ChildrenPoolGenerator @Inject constructor(private val genderPoolGenerator: GenderPoolGenerator,
                                                private val abilityPoolGenerator: AbilityPoolGenerator,
                                                private val naturePoolGenerator: NaturePoolGenerator,
                                                private val ivPoolGenerator: IvPoolGenerator,
                                                private val externalRepository: ExternalRepository,
                                                private val ivChanceChecker: IvChanceChecker,
                                                private val genderChanceChecker: GenderChanceChecker,
                                                private val natureChanceChecker: NatureChanceChecker,
                                                private val abilityChanceChecker: AbilityChanceChecker) {

    fun withAbility(related: InternalPokemon, target: InternalPokemon)
            = abilityChanceChecker.getAbilityMultiplier(related, target)

    fun withNature(related: InternalPokemon, compatible: InternalPokemon, target: InternalPokemon)
            = natureChanceChecker.getNatureChanceMultiplier(related, compatible, target)

    fun withGender(target: InternalPokemon)
            = genderChanceChecker.getGenderChance(target)


    //TODO: Verify if multiplier always matches total chance
    fun generate(couple: Couple, goal: InternalPokemon): Pair<ArrayList<Child>, Int> {

        val pool = ArrayList<Child>()

        val genderPool = genderPoolGenerator.generate(couple)
        val abilitySlotPool = abilityPoolGenerator.generate(couple)
        val naturePool = naturePoolGenerator.generate(goal)
        val ivPool = ivPoolGenerator.generate(couple)


        val baseFormId = externalRepository.getBaseFormId(couple.related.externalId)


        for (gender in genderPool) {
            for (abilitySlot in abilitySlotPool) {
                for (nature in naturePool) {
                    for (IVs in ivPool) {
                        val createdPokemon = InternalPokemon(baseFormId)
                        createdPokemon.IVs = IVs
                        createdPokemon.natureId = nature
                        createdPokemon.abilitySlot = abilitySlot
                        createdPokemon.gender = gender


                        val chance = ivChanceChecker.getStrictIvChance(
                                couple.related.IVs,
                                couple.compatible!!.IVs,
                                createdPokemon.IVs)

                        chance * withGender(createdPokemon)
                        chance * withAbility(couple.related, createdPokemon)
                        chance * withNature(couple.related, couple.compatible!!, createdPokemon)

                        pool.add(Child(createdPokemon, chance))
                    }
                }
            }
        }

        val multiplier = genderPool.size * abilitySlotPool.size * naturePool.size

        return Pair(pool, multiplier)
    }

}