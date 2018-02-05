package marcelo.breguenait.breedingassistant.logic

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon


class Child(child: InternalPokemon, var generationChance: Double) {

    private val childCombination: CombinationHolder = CombinationHolder(Couple(child))


    val childPokemon: InternalPokemon
        get() = childCombination.couple.related

    fun setChance(chance: Double) {
        this.generationChance = chance
    }

    val weightedChance: Double
        get() = generationChance * childCombination.chance

    fun setCombination(pokemon: InternalPokemon, chance: Double) {
        childCombination.setCompatibleParent(pokemon)
        childCombination.setCombinedChildrenChance(chance)
    }
}
