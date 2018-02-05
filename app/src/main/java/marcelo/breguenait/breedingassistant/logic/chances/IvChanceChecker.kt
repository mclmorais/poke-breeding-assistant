package marcelo.breguenait.breedingassistant.logic.chances

import java.util.*
import javax.inject.Inject

class IvChanceChecker @Inject constructor() {

    private val destinyKnotCombinations = ArrayList<IntArray>(6)


    init {
        fillCombinations()
    }

    fun getIvChance(firstIVs: IntArray, secondIVs: IntArray, goalIVs: IntArray): Double {

        val MAX = 1.0
        val MIN = 0.0
        val HALF = 0.5
        val RANDOM = 1.0 / 32.0

        val ivs = 0..5

        return destinyKnotCombinations.sumByDouble { currentCombination ->
            ivs.fold(1.0, { total, currentIV ->
                total *
                        if (goalIVs[currentIV] != 1)
                            MAX
                        else if (currentCombination[currentIV] == 0)
                            RANDOM
                        else if (firstIVs[currentIV] == 1 && secondIVs[currentIV] == 1)
                            MAX
                        else if (firstIVs[currentIV] != secondIVs[currentIV])
                            HALF
                        else
                            MIN
            })
        } / destinyKnotCombinations.size.toDouble()
    }

    fun getStrictIvChance(firstIVs: IntArray, secondIVs: IntArray, targetIVs: IntArray): Double {

        val MAX = 1.0
        val MIN = 0.0
        val HALF = 0.5
        val RANDOM = 1.0 / 32.0

        val ivs = 0..5

        return destinyKnotCombinations.sumByDouble { currentCombination ->

            ivs.fold(1.0, { total, currentIV ->
                total *
                        if (currentCombination[currentIV] == 0)
                            if (targetIVs[currentIV] == 1) RANDOM else (1 - RANDOM)
                        else if (firstIVs[currentIV] == targetIVs[currentIV] && secondIVs[currentIV] == targetIVs[currentIV])
                            MAX
                        else if (firstIVs[currentIV] != secondIVs[currentIV])
                            HALF
                        else
                            MIN
            })

        } / destinyKnotCombinations.size.toDouble()
    }

    private fun fillCombinations() {

        destinyKnotCombinations.add(intArrayOf(1, 1, 1, 1, 1, 0))
        destinyKnotCombinations.add(intArrayOf(1, 1, 1, 1, 0, 1))
        destinyKnotCombinations.add(intArrayOf(1, 1, 1, 0, 1, 1))
        destinyKnotCombinations.add(intArrayOf(1, 1, 0, 1, 1, 1))
        destinyKnotCombinations.add(intArrayOf(1, 0, 1, 1, 1, 1))
        destinyKnotCombinations.add(intArrayOf(0, 1, 1, 1, 1, 1))
    }
}