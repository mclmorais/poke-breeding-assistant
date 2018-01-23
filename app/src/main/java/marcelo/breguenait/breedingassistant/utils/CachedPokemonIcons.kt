package marcelo.breguenait.breedingassistant.utils


import android.content.Context
import android.util.SparseIntArray
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import java.util.*


class CachedPokemonIcons(
    private val externalRepository: ExternalRepository,
    private val context: Context
) {

    private val idsMap = SparseIntArray()


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

            idsMap.append(pokemon.id, iconId)
        }
        idsMap.append(-1, R.drawable.missingno)


    }

    fun getIconId(pokemonId: Int): Int {
        return idsMap[pokemonId, -1]
    }


}