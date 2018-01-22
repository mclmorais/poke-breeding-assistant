package marcelo.breguenait.breedingassistant.data.external

import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalAbility
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import marcelo.breguenait.breedingassistant.logic.CompatibilityChecker
import java.util.*

/**
 * Created by Marcelo on 14/01/2018.
 */
class ExternalRepository(dataSource: ExternalPokemonDataSource) {
    private val abilities: LinkedHashMap<Int, ExternalAbility> =
        dataSource.loadExternalAbilities<LinkedHashMap<Int, ExternalAbility>>()

    private val pokemons: LinkedHashMap<Int, ExternalPokemon> = dataSource.loadExternalPokemons<LinkedHashMap<Int, ExternalPokemon>>()
    private val compatibilityChecker = CompatibilityChecker(this) //TODO: mudar pra injection?

    val externalPokemons: ArrayList<ExternalPokemon>
        get() = ArrayList(pokemons.values)

    fun getExternalPokemon(id: Int): ExternalPokemon? {
        return pokemons[id]
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

}