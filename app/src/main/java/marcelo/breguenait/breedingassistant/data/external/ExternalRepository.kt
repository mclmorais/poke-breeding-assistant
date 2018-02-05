package marcelo.breguenait.breedingassistant.data.external

import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalAbility
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalStat
import marcelo.breguenait.breedingassistant.logic.CompatibilityChecker
import java.util.*

/**
 * Created by Marcelo on 14/01/2018.
 */
class ExternalRepository(dataSource: ExternalPokemonDataSource) {
    private val abilities: LinkedHashMap<Int, ExternalAbility> =
        dataSource.loadExternalAbilities<LinkedHashMap<Int, ExternalAbility>>()

    private val pokemons: LinkedHashMap<Int, ExternalPokemon> = dataSource.loadExternalPokemons<LinkedHashMap<Int, ExternalPokemon>>()

    val natures: LinkedHashMap<Int, ExternalNature> = dataSource.loadExternalNatures<LinkedHashMap<Int, ExternalNature>>()

    private val stats: LinkedHashMap<Int, ExternalStat> = dataSource.loadExternalStats<LinkedHashMap<Int, ExternalStat>>()

    private val compatibilityChecker = CompatibilityChecker(this) //TODO: mudar pra injection?

    val externalPokemons: ArrayList<ExternalPokemon>
        get() = ArrayList(pokemons.values)

    fun getExternalPokemon(id: Int): ExternalPokemon? {
        return pokemons[id]
    }

    fun getNature(natureId: Int): ExternalNature {
        return natures[natureId] ?: ExternalNature()
    }

    fun getAllFromEggGroups(pokemonId: Int): List<ExternalPokemon> {

        val pokemonList = ArrayList(pokemons.values)

        return compatibilityChecker.getCompatibleExternalPokemon(pokemonList, getExternalPokemon(pokemonId)!!)

    }

    fun getAllFromSameFamily(pokemonId: Int): List<ExternalPokemon> {

        val pokemonList = ArrayList(pokemons.values)

        val familyList: ArrayList<ExternalPokemon> = compatibilityChecker.getRelatedExternalPokemons(pokemonList, getExternalPokemon(pokemonId) ?: return emptyList()) as ArrayList<ExternalPokemon>

        pokemons[132]?.let { familyList.add(0, it) }

        return familyList

    }

    fun getAbilitiesNames(pokemonId: Int): Array<String?> {
        val abilityIds = pokemons[pokemonId]?.abilities ?: return emptyArray()
        val abilityNames = arrayOfNulls<String>(3)


        abilityIds.indices
            .filter {
                abilityIds[it] != 0
            }
            .forEach {
                abilityNames[it] = abilities[abilityIds[it]]?.name ?: ""
            } //TODO: do a better check?
        return abilityNames

    }

    fun getStatName(statId: Int, languageId: Int): String {
        return stats[statId]?.getName(languageId) ?: ""
    }

    fun getAbility(pokemonId: Int, abilitySlot: Int): ExternalAbility? { //TODO: remove from here so that null checks may be done by the caller
        return abilities[pokemons[pokemonId]?.getAbilityId(abilitySlot)]
    }

    fun getGenderRestriction(pokemonId: Int): Int? { //TODO: remove from here so that null checks may be done by the caller
        return pokemons[pokemonId]?.genderRestriction
    }

    fun getNumberOfAbilities(pokemonId: Int): Int { //TODO: remove from here so that null checks may be done by the caller
        return if (pokemons[pokemonId]?.abilities?.get(1) == 0) 1 else 2
    }

    fun getBaseFormId(pokemonId: Int): Int { //TODO: remove from here so that null checks may be done by the caller

        var currentPokemon = pokemons[pokemonId]

        var tries = 0
        while (currentPokemon?.previousEvolution != 0) {
            tries++
            currentPokemon = pokemons[currentPokemon?.previousEvolution]
            if (tries > 10) break
        }

        return currentPokemon?.id ?: 151 //TODO: make better
    }

}