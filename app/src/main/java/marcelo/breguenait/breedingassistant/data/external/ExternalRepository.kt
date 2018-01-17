package marcelo.breguenait.breedingassistant.data.external

import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalAbility
import java.util.*

/**
 * Created by Marcelo on 14/01/2018.
 */
class ExternalRepository(dataSource: ExternalPokemonDataSource) {
    private val abilities: LinkedHashMap<Int, ExternalAbility> =
        dataSource.loadExternalAbilities<LinkedHashMap<Int, ExternalAbility>>()
}