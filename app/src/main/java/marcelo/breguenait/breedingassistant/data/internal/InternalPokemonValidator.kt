package marcelo.breguenait.breedingassistant.data.internal

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.utils.Genders

class InternalPokemonValidator{


    companion object {

        fun validate(internalPokemon: InternalPokemon, repository: ExternalRepository): Boolean {

            repository.getExternalPokemon(internalPokemon.externalId) ?: return false

            repository.getNature(internalPokemon.natureId) ?: return false

            if(internalPokemon.abilitySlot !in 0..2) return false

            repository.getAbility(internalPokemon.externalId, internalPokemon.abilitySlot) ?: return false

            return true

        }

        @Genders.GendersFlag
        fun getValidatedGender(id: Int, @Genders.GendersFlag gender: Int, repository: ExternalRepository): Int {

            val restriction = repository.getExternalPokemon(id)?.genderRestriction ?: throw Exception("Invalid pokemon Id")

            if (restriction in 1..999)
                return gender
            else
                return restriction
        }
    }

}