package marcelo.breguenait.breedingassistant.logic

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.utils.Utility
import javax.inject.Inject

class AssistantAi @Inject constructor(private val breedingManager: BreedingManager) {


    var directCombinations: List<CombinationHolder> = ArrayList()
        private set

    var improvementCombinations: List<CombinationHolder> = ArrayList()
        private set

    fun calculateBestMatches(goal: InternalPokemon) {
        try {
            var direct = breedingManager.sortChances(breedingManager.directChances)
            direct = direct.subList(0, Utility.upTo(1, direct.size))

            for (combination in direct)
                ChanceProblemInvestigator.addCombinationProblems(combination, goal)

            directCombinations = direct

            var improvements = breedingManager.sortChances(breedingManager.improvementChances)
            improvements = ArrayList(improvements.subList(0, Utility.upTo(2, improvements.size)))



            if (direct.any()) {
                improvements = improvements.filterNot {
                        improvement ->
                    direct[0].couple.related.internalId == improvement.couple.related.internalId &&
                            direct[0].couple.compatible?.internalId == improvement.couple.compatible?.internalId
                }
            }


            improvementCombinations = improvements
                .filter { improvement ->
                    val baseDirectChance = direct[0].chance

                    val doubleDirectChance = 1.0 - (1.0 - baseDirectChance) * (1.0 - baseDirectChance)

                    improvement.chance > doubleDirectChance
                }
                .filterNot {
                    java.lang.Double.compare(it.chance, 0.0) <= 0
                }




        } catch (e: CompatibilityChecker.CompatibilityException) {
            e.printStackTrace()
        }

    }
//    fun calculateBestMatches(goal: InternalPokemon) {
//        try {
//            var direct = breedingManager.sortChances(breedingManager.directChances)
//            direct = direct.subList(0, Utility.upTo(1, direct.size))
//
//            for (combination in direct)
//                ChanceProblemInvestigator.addCombinationProblems(combination, goal)
//
//            directCombinations = direct
//
//            var improvements = breedingManager.sortChances(breedingManager.improvementChances)
//            improvements = ArrayList(improvements.subList(0, Utility.upTo(2, improvements.size)))
//
//
//
//            if (direct.any()) {
//                improvements = improvements.filterNot { improvement ->
//                    direct[0].couple.related.internalId == improvement.couple.related.internalId &&
//                            direct[0].couple.compatible?.internalId == improvement.couple.compatible?.internalId
//                }
//
//            }
//
//                improvementCombinations =
//                        improvements.filter {
//                            val baseDirectChance = direct[0].chance //TODO: BUG MAY HAPPEN HERE!! (direct[0] MAY NOT EXIST)
//                            val doubleDirectChance = 1.0 - (1.0 - baseDirectChance) * (1.0 - baseDirectChance)
//
//                            (it.chance > doubleDirectChance) && ( java.lang.Double.compare(it.chance, 0.0) <= 0)
//                        }
//
//
//
//        } catch (e: CompatibilityChecker.CompatibilityException) {
//            e.printStackTrace()
//        }
//
//    }

    val directFlag: Int
        get() = breedingManager.directFlag
}
