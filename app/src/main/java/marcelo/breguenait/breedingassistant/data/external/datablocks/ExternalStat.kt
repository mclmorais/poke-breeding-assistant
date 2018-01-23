package marcelo.breguenait.breedingassistant.data.external.datablocks

import java.util.HashMap

/**
 * Created by Marcelo on 20/02/2017.
 */

class ExternalStat(val id: Int, internal val names: HashMap<Int, String>) {

    fun getName(languageId: Int): String {
        return if (names.containsKey(languageId))
            names[languageId] ?: ""
        else
            names[9] ?: ""
    }
}
