package marcelo.breguenait.breedingassistant.data.external.datablocks

import java.util.HashMap

/**
 * Holds information retrieved from external files about a nature.
 */
class ExternalNature(val id: Int, val name: String, val increasedStatId: Int, val decreasedStatId: Int, val names: HashMap<Int, String>) {

    constructor(): this(-1, "", -1, -1, HashMap<Int, String>(0))

fun getName(languageId: Int): String {
    return names.get(languageId) ?: ""
}

}