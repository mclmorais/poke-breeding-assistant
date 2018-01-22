package marcelo.breguenait.breedingassistant.logic

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.utils.Genders.*
import javax.inject.Inject

//TODO: 100% COVERAGE ON THIS CLASS!
class CompatibilityChecker @Inject constructor(private val externalRepository: ExternalRepository) {

    /**
     * Returns a list of pokemon related with [target] from the received [candidates].
     */
    fun getRelatedStoredPokemons(candidates: List<InternalPokemon>, target: InternalPokemon): List<InternalPokemon> {

        val relatedPokemons = ArrayList<InternalPokemon>(candidates.size)

        candidates.filterTo(relatedPokemons) {
            checkFamilyCompatibility(externalRepository.getExternalPokemon(it.externalId),
                                     externalRepository.getExternalPokemon(target.externalId))
        }

        return relatedPokemons
    }

    fun getRelatedExternalPokemons(candidates: List<ExternalPokemon>, target: ExternalPokemon): List<ExternalPokemon> {

        val relatedPokemons = ArrayList<ExternalPokemon>(candidates.size)

        candidates.filterTo(relatedPokemons) { checkFamilyCompatibility(it, target) }

        return relatedPokemons
    }

    /**
     * Returns a list of pokemon compatible with [target] from the received [candidates].
     */
    fun getCompatibleStoredPokemon(candidates: List<InternalPokemon>, target: InternalPokemon): List<InternalPokemon> {

        val compatiblePokemons = ArrayList<InternalPokemon>(candidates.size)

        candidates.filterTo(compatiblePokemons) {
            checkEggGroupCompatibility(externalRepository.getExternalPokemon(it.externalId),
                                       externalRepository.getExternalPokemon(target.externalId))
            ||
                    checkFamilyCompatibility(externalRepository.getExternalPokemon(it.externalId),
                                             externalRepository.getExternalPokemon(target.externalId))
        }

        return compatiblePokemons
    }

    /**
     * Returns a list of pokemon compatible with [target] from the received [candidates].
     */
    fun getCompatibleExternalPokemon(candidates: List<ExternalPokemon>, target: ExternalPokemon): List<ExternalPokemon> {

        val compatiblePokemons = ArrayList<ExternalPokemon>(candidates.size)

        candidates.filterTo(compatiblePokemons) {
            checkEggGroupCompatibility(it, target)
        }

        return compatiblePokemons
    }

    private fun checkFamilyCompatibility(potentialPokemonId: ExternalPokemon?, targetPokemonId: ExternalPokemon?): Boolean {

        val potentialChain = potentialPokemonId?.evolutionChainId
        val targetChain = targetPokemonId?.evolutionChainId

        if (potentialChain != null && targetChain != null)
            return potentialChain == targetChain
        else
            return false
    }

    private fun checkEggGroupCompatibility(potentialPokemon: ExternalPokemon?, target: ExternalPokemon?): Boolean {

        val potentialEggGroups = potentialPokemon?.eggGroups ?: return false
        val targetEggGroups = target?.eggGroups ?: return false

        if (15 in targetEggGroups) return false

        if (13 in potentialEggGroups) return true

        potentialEggGroups.forEach {
            if (it in targetEggGroups.filter { it != 0 }) return true
        }

        return false
    }

    /**
     * Checks if a [couple] contains a valid gender combination.
     */
    @Throws(CompatibilityException::class)
    fun checkGenderCompatibility(couple: Couple): Boolean {

        val related = couple.related
        val compatible = couple.compatible ?: throw CompatibilityException("No compatible pokemon", couple)

        when (related.gender) {
            FEMALE     -> return compatible.gender == MALE || compatible.gender == DITTO
            MALE       -> return compatible.gender == DITTO
            GENDERLESS -> return compatible.gender == DITTO
            DITTO      -> throw CompatibilityException("Ditto was related parent", couple)
            else       -> throw CompatibilityException("Unable to determine gender compatibility", couple)
        }

    }

    internal inner class CompatibilityException(message: String, val couple: Couple) : Exception(message)

}
