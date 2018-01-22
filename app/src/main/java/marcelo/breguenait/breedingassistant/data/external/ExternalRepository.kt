package marcelo.breguenait.breedingassistant.data.external

import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalAbility
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import java.util.*

/**
 * Created by Marcelo on 14/01/2018.
 */
class ExternalRepository(dataSource: ExternalPokemonDataSource) {
    private val abilities: LinkedHashMap<Int, ExternalAbility> =
        dataSource.loadExternalAbilities<LinkedHashMap<Int, ExternalAbility>>()

    private val pokemons: LinkedHashMap<Int, ExternalPokemon> = dataSource.loadExternalPokemons<LinkedHashMap<Int, ExternalPokemon>>()


    val externalPokemons: ArrayList<ExternalPokemon>
        get() = ArrayList(pokemons.values)
}