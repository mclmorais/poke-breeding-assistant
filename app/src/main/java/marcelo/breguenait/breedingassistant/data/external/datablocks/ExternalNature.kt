package marcelo.breguenait.breedingassistant.data.external.datablocks

import android.util.SparseArray
import java.util.HashMap

/**
 * Holds information retrieved from external files about a nature.
 */
class ExternalNature(val id: Int, val name: String, val increasedStatId: Int, val decreasedStatId: Int, private val names: SparseArray<String>) {

    constructor(): this(-1, "", -1, -1, SparseArray<String>(0))

fun getName(languageId: Int = 9): String {
    return names.get(languageId) ?: ""
}

}