package marcelo.breguenait.breedingassistant.logic

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon

internal object ChanceProblemInvestigator {


    fun addCombinationProblems(combination: CombinationHolder, goal: InternalPokemon): CombinationHolder {

        val combinationProblems = CombinationProblems()

        combinationProblems.setNoMatchingAbility(noMatchingAbility(combination, goal))
        combinationProblems.setNoMatchingNature(noMatchingNature(combination, goal))
        combinationProblems.missingIVs = missingIVs(combination, goal)

        combination.combinationProblems = combinationProblems

        return combination
    }


    private fun noMatchingNature(combination: CombinationHolder, goal: InternalPokemon): Boolean {
        val related = combination.couple.related
        val compatible = combination.couple.compatible

        return !(related.natureId == goal.natureId || compatible!!.natureId == goal.natureId)
    }

    private fun noMatchingAbility(combination: CombinationHolder, goal: InternalPokemon): Boolean {
        val related = combination.couple.related

        return related.abilitySlot != goal.abilitySlot
    }

    private fun missingIVs(combination: CombinationHolder, goal: InternalPokemon): IntArray {
        val relatedIVs = combination.couple.related.IVs
        val compatibleIVs = combination.couple.compatible!!.IVs
        val goalIVs = goal.IVs

        val missingIVs = IntArray(6)


        /*For each IV, if the goal has it but neither parent has it, it's missing*/
        (0..5)
                .filter { goalIVs[it] == 1 }
                .forEach { missingIVs[it] = if (relatedIVs[it] == 0 && compatibleIVs[it] == 0) 1 else 0 }

        return missingIVs
    }


}
