package marcelo.breguenait.breedingassistant.data.external.datablocks

import java.util.ArrayList

/**
 * Holds generic information retrieved from external files about a pokemon.
 */
class ExternalPokemon(
    val id: Int,
    val number: Int,
    val name: String,
    val genderRestriction: Int,
    val types: IntArray,
    val abilities: IntArray,
    val eggGroups: IntArray,
    val evolutionChainId: Int,
    val previousEvolution: Int,
    val possibleEvolutions: ArrayList<Int>,
    val stats: IntArray
) {

    constructor(id: Int) : this(
        id,
        -1,
        "",
        -1,
        intArrayOf(-1),
        intArrayOf(-1),
        intArrayOf(-1),
        -1,
        -1,
        arrayListOf(-1),
        intArrayOf(-1)
    )


    fun getAbilityId(slot: Int): Int {
        return abilities[slot]
    }

    fun getStat(statId: Int): Int {
        return stats[statId]
    }
}