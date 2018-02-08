package marcelo.breguenait.breedingassistant.logic

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.screens.assistant.adapter.AdapterConstants
import marcelo.breguenait.breedingassistant.screens.assistant.adapter.ViewType
import marcelo.breguenait.breedingassistant.utils.Sequence


class CombinationHolder(val couple: Couple) : ViewType {

    val id: Int = Sequence.nextValue()

    var children: List<Child>? = null
        private set

    var chance = 0.0
        private set

    var combinationProblems: CombinationProblems? = null

    internal fun setChildren(children: List<Child>?, multiplier: Int) {
        this.children = children

        if (children != null) {
            for (child in this.children!!) {
                chance += child.weightedChance
            }
        }
        chance /= multiplier.toDouble()
    }

    internal fun setCompatibleParent(compatible: InternalPokemon) {
        couple.compatible = compatible
    }

    internal fun setCombinedChildrenChance(combinedChildrenChance: Double) {
        this.chance = combinedChildrenChance
    }

    override val viewType: Int
        get() {
            return if(children == null)
                AdapterConstants.DIRECT_SUGGESTION
            else
                AdapterConstants.IMPROVEMENT_SUGGESTION
        }
}
