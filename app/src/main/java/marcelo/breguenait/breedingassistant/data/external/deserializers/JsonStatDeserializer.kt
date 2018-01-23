package marcelo.breguenait.breedingassistant.data.external.deserializers

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type
import java.util.HashMap
import java.util.LinkedHashMap

import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalStat

/**
 * Created by Marcelo on 10/02/2017.
 */

class JsonStatDeserializer : JsonCustomDeserializer<LinkedHashMap<Int, ExternalStat>> {

    override val type = object : TypeToken<LinkedHashMap<Int, ExternalStat>>() {}.type

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): LinkedHashMap<Int, ExternalStat> {
        val jsonObject = json.asJsonObject
        val jsonArray = jsonObject.get("stats").asJsonArray//jsonObject.get("stats").getAsJsonArray();

        val statData = LinkedHashMap<Int, ExternalStat>()

        for (statJsonElement in jsonArray) {

            val statJsonObject = statJsonElement as JsonObject

            val nameArray = statJsonObject.get("names").asJsonArray

            val names = HashMap<Int, String>(10)
            for (nameElement in nameArray) {
                val id = nameElement.asJsonObject.get("id").asInt
                val name = nameElement.asJsonObject.get("name").asString
                names[id] = name
            }

            val id = statJsonObject.get("stat_id").asInt

            val deserializedStatBlock = ExternalStat(id, names)

            statData[deserializedStatBlock.id] = deserializedStatBlock

        }

        return statData
    }
}