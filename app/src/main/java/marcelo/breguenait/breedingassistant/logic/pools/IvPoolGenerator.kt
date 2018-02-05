package marcelo.breguenait.breedingassistant.logic.pools

import marcelo.breguenait.breedingassistant.logic.Couple
import marcelo.breguenait.breedingassistant.logic.chances.IvChanceChecker
import java.util.*
import javax.inject.Inject

class IvPoolGenerator @Inject constructor(private val ivChanceChecker: IvChanceChecker) {

    private val ivCombinations: ArrayList<IntArray>

    init {
        val hypotheticalIVs = ArrayList<IntArray>()

        for (i in 0..63) {
            val IVs = IntArray(6)
            for (j in 0..5) {
                IVs[j] = (i shr j) and 1
            }
            hypotheticalIVs.add(IVs)
        }

        this.ivCombinations = hypotheticalIVs
    }

    fun generateAll() : List<IntArray> {

        return ivCombinations

    }

    fun generate(couple: Couple): List<IntArray> {

        val relatedIVs = couple.related.IVs
        val compatibleIVs = couple.compatible?.IVs ?: return emptyList()

        return ivCombinations.filter {
            ivChanceChecker.getStrictIvChance(relatedIVs, compatibleIVs, it) > 0
        }
    }
}