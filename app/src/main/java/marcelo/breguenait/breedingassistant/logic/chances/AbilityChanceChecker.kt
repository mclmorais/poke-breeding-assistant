package marcelo.breguenait.breedingassistant.logic.chances

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.utils.Genders
import javax.inject.Inject


class AbilityChanceChecker @Inject constructor(val externalRepository: ExternalRepository) {

    val CHANCE_EITHER_PASSING_SINGLE_ABILITY = 1.0

    val CHANCE_FEMALE_PASSING_DOUBLE_ABILITY = 0.8
    val CHANCE_FEMALE_PASSING_HIDDEN_ABILITY = 0.8

    val CHANCE_MALE_PASSING_DOUBLE_ABILITY = 0.5
    val CHANCE_MALE_PASSING_HIDDEN_ABILITY = 0.2

    fun getAbilityMultiplier(relatedPokemon: InternalPokemon, numOfAbilities: Int, targetPokemon: InternalPokemon): Double {
        if (targetPokemon.abilitySlot == 2) return findChanceForHiddenAbility(relatedPokemon)
        else {
            if (numOfAbilities == 1) return findChanceForSingleSlotAbility(relatedPokemon)
            else return findChanceForDoubleSlotAbility(relatedPokemon, targetPokemon)
        }
    }

    fun getAbilityMultiplier(relatedPokemon: InternalPokemon, targetPokemon: InternalPokemon): Double {

        val numOfAbilities = externalRepository.getNumberOfAbilities(relatedPokemon.externalId)

        if (targetPokemon.abilitySlot == 2) return findChanceForHiddenAbility(relatedPokemon)
        else {
            if (numOfAbilities == 1) return findChanceForSingleSlotAbility(relatedPokemon)
            else return findChanceForDoubleSlotAbility(relatedPokemon, targetPokemon)
        }

    }


    private fun findChanceForHiddenAbility(relatedPokemon: InternalPokemon): Double {
        if (relatedPokemon.abilitySlot == 2) {
            if (relatedPokemon.gender == Genders.FEMALE) {
                return CHANCE_FEMALE_PASSING_HIDDEN_ABILITY
            } else {
                return CHANCE_MALE_PASSING_HIDDEN_ABILITY
            }
        } else return 0.0
    }

    private fun findChanceForSingleSlotAbility(relatedPokemon: InternalPokemon): Double {
        if (relatedPokemon.abilitySlot != 2) {
            return CHANCE_EITHER_PASSING_SINGLE_ABILITY
        } else {
            if (relatedPokemon.gender == Genders.FEMALE) {
                return (1.0 - CHANCE_FEMALE_PASSING_HIDDEN_ABILITY)
            } else {
                return (1.0 - CHANCE_MALE_PASSING_HIDDEN_ABILITY)
            }
        }
    }

    private fun findChanceForDoubleSlotAbility(relatedPokemon: InternalPokemon, targetPokemon: InternalPokemon): Double {
        if (relatedPokemon.abilitySlot != 2) {
            if (relatedPokemon.gender == Genders.FEMALE) {
                if (relatedPokemon.abilitySlot == targetPokemon.abilitySlot) {
                    return CHANCE_FEMALE_PASSING_DOUBLE_ABILITY
                } else {
                    return (1 - CHANCE_FEMALE_PASSING_DOUBLE_ABILITY)
                }
            } else return CHANCE_MALE_PASSING_DOUBLE_ABILITY
        } else {
            if (relatedPokemon.gender == Genders.FEMALE) {
                return ((1.0 - CHANCE_FEMALE_PASSING_HIDDEN_ABILITY) / 2.0)
            } else {
                return ((1.0 - CHANCE_MALE_PASSING_HIDDEN_ABILITY) / 2.0)
            }
        }

    }

    fun Double.withAbility(first : InternalPokemon, target: InternalPokemon): Double {
        return this * getAbilityMultiplier(first, target)
    }

}