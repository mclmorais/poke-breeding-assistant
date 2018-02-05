package marcelo.breguenait.breedingassistant.logic.pools

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.logic.Couple
import marcelo.breguenait.breedingassistant.utils.Genders.*
import javax.inject.Inject

class GenderPoolGenerator @Inject constructor(private val externalRepository: ExternalRepository) {

    /**
     * Generates an array with all possible genders that the base form of the [couple] can have.
     */
    fun generate(couple: Couple): IntArray {

        val baseFormId = externalRepository.getBaseFormId(couple.related.externalId)

        val genderRestriction = externalRepository.getGenderRestriction(baseFormId)

        if (genderRestriction in (MALE + 1)..(FEMALE - 1))
            return intArrayOf(MALE, FEMALE)
        else
            return intArrayOf(GENDERLESS)
    }
}