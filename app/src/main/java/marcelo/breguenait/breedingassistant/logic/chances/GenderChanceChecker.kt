package marcelo.breguenait.breedingassistant.logic.chances

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.utils.Genders.FEMALE
import marcelo.breguenait.breedingassistant.utils.Genders.MALE
import javax.inject.Inject


class GenderChanceChecker @Inject constructor(private val externalRepository: ExternalRepository) {

    fun getGenderChance(target: InternalPokemon): Double {

        val genderRestriction = externalRepository.getGenderRestriction(target.externalId) ?: return 0.0 //TODO: betteringness

        if (target.gender == genderRestriction)
            return 1.0
        else if (genderRestriction in 1..999) {
            if (target.gender == MALE)
                return genderRestriction / 1000.0
            else if (target.gender == FEMALE)
                return (1000.0 - genderRestriction.toDouble()) / 1000.0
        }

        return 0.0
    }

}
