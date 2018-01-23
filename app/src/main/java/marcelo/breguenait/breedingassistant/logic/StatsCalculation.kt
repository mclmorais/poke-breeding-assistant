package marcelo.breguenait.breedingassistant.logic

import javax.inject.Inject

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature

class StatsCalculation @Inject
constructor(var externalRepository: ExternalRepository) {


    fun getStat(pokemonId: Int, statId: Int, level: Int, natureId: Int, IV: Int, EV: Int): Int {

        val nature = externalRepository.getNature(natureId)

        val baseStat = externalRepository.getExternalPokemon(pokemonId)!!.getStat(statId)

        val increasedStatId = nature.increasedStatId

        val decreasedStatId = nature.decreasedStatId

        var natureMultiplier = 1.0
        if (increasedStatId != decreasedStatId) {
            if (statId == increasedStatId)
                natureMultiplier = 1.1
            else if (statId == decreasedStatId) natureMultiplier = 0.9
        }

        return if (statId == 0) {
            (((2 * baseStat).toDouble() + IV.toDouble() + EV / 4.0) * level / 100 + 10.0 + level.toDouble()).toInt()
        } else {
            ((((2 * baseStat).toDouble() + IV.toDouble() + EV / 4.0) * level / 100 + 5) * natureMultiplier).toInt()
        }

    }


}