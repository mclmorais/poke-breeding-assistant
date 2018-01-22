package marcelo.breguenait.breedingassistant.utils


import android.content.Context
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import java.util.*


class CachedPokemonIcons(
    private val externalRepository: ExternalRepository,
    private val context: Context
) {

    private val idsMap = HashMap<Int, Int>()


    init {
        fillIdsList()
    }

    private fun fillIdsList() {

        val pokemons = externalRepository.externalPokemons

        for (pokemon in pokemons) {
            val pokemonId = pokemon.id
            val iconIdString = context.getString(R.string.pkmn_link_filename, pokemonId)
            val iconId = context.resources.getIdentifier(
                iconIdString,
                context.getString(R.string.drawable_resource_name),
                context.packageName
            )
            idsMap[pokemon.id] = iconId
        }
        idsMap[-1] = R.drawable.missingno


    }

    fun getIconId(pokemonId: Int): Int {
        return if (idsMap.containsKey(pokemonId))
            idsMap[pokemonId] ?: -1
        else
            idsMap[-1] ?: -1
    }


}