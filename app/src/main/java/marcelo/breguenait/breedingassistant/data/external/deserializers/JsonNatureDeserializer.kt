package marcelo.breguenait.breedingassistant.data.external.deserializers

import android.util.SparseIntArray
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
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon

/**
 * Created by Marcelo on 10/02/2017.
 */

class JsonNatureDeserializer : JsonCustomDeserializer<LinkedHashMap<Int, ExternalNature>> {


    override val type: Type = object : TypeToken<LinkedHashMap<Int, ExternalNature>>() {}.type
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): LinkedHashMap<Int, ExternalNature> {

        val jsonObject = json.asJsonObject
        val jsonArray = jsonObject.get("natures").asJsonArray

        val natureData = LinkedHashMap<Int, ExternalNature>()

        for (natureJsonElement in jsonArray) {

            val natureJsonObject = natureJsonElement as JsonObject

            val nameArray = natureJsonObject.get("names").asJsonArray

            val names = HashMap<Int, String>(10)
            for (nameElement in nameArray) {
                val id = nameElement.asJsonObject.get("id").asInt
                val name = nameElement.asJsonObject.get("name").asString
                names[id] = name
            }

            val deserializedNatureBlock = ExternalNature(natureJsonObject.get("id").asInt,
                natureJsonObject.get("identifier").asString,
                natureJsonObject.get("increased_stat_id").asInt,
                natureJsonObject.get("decreased_stat_id").asInt,
                names)

            natureData[natureJsonObject.get("id").asInt] = deserializedNatureBlock

        }

        return natureData
    }

    //    @Throws(JsonParseException::class)
//    fun deserialize(
//        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LinkedHashMap<Int, ExternalNature> {
//        val jsonObject = json.asJsonObject
//        val jsonArray = jsonObject.get("natures").asJsonArray
//
//        val natureData = LinkedHashMap<Int, ExternalNature>()
//
//        for (natureJsonElement in jsonArray) {
//
//            val natureJsonObject = natureJsonElement as JsonObject
//
//            val nameArray = natureJsonObject.get("names").asJsonArray
//
//            val names = HashMap<Int, String>(10)
//            for (nameElement in nameArray) {
//                val id = nameElement.asJsonObject.get("id").asInt
//                val name = nameElement.asJsonObject.get("name").asString
//                names[id] = name
//            }
//
//            val deserializedNatureBlock = ExternalNature(natureJsonObject.get("id").asInt,
//                natureJsonObject.get("identifier").asString,
//                natureJsonObject.get("increased_stat_id").asInt,
//                natureJsonObject.get("decreased_stat_id").asInt,
//                names)
//
//            natureData[natureJsonObject.get("id").asInt] = deserializedNatureBlock
//
//        }
//
//        return natureData
//    }

}