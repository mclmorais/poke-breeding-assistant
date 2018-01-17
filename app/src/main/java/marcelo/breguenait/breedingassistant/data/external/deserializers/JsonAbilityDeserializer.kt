package marcelo.breguenait.breedingassistant.data.external.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalAbility
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Marcelo on 14/01/2018.
 */
class JsonAbilityDeserializer : JsonCustomDeserializer<LinkedHashMap<Int, ExternalAbility>> {

    override val type: Type = object : TypeToken<LinkedHashMap<Int, ExternalAbility>>() {}.type

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LinkedHashMap<Int, ExternalAbility> {
        val jsonObject = json?.asJsonObject
        val jsonArray = jsonObject?.get("abilities")?.asJsonArray

        val abilityData = LinkedHashMap<Int, ExternalAbility>(260)

        if(jsonObject != null && jsonArray != null) {
            for (abilityJsonElement in jsonArray) {

                val abilityJsonObject = abilityJsonElement as JsonObject

                val deserializedAbilityBlock = ExternalAbility(abilityJsonObject.get("num").asInt,
                        abilityJsonObject.get("name").asString)

                abilityData[abilityJsonObject.get("num").asInt] = deserializedAbilityBlock

            }

            return abilityData
        }
        else {
            throw Exception("External data not found")
        }
    }
}
