package marcelo.breguenait.breedingassistant.data.external.datablocks

import android.util.SparseArray
import androidx.util.containsKey
import java.util.HashMap

/**
 * Created by Marcelo on 20/02/2017.
 */

class ExternalStat(val id: Int, internal val names: SparseArray<String>) {

    fun getName(languageId: Int): String {
        return if (names.containsKey(languageId))
            names[languageId] ?: ""
        else
            names[9] ?: ""
    }
}
