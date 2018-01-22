package marcelo.breguenait.breedingassistant.data.external.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Marcelo on 22/01/2018.
 */
class JsonPokedexDeserializer : JsonCustomDeserializer<LinkedHashMap<Int, ExternalPokemon>> {

    override val type: Type = object : TypeToken<LinkedHashMap<Int, ExternalPokemon>>() {}.type

    fun getDataBlockClass(): Class<*> {
        return ExternalPokemon::class.java
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LinkedHashMap<Int, ExternalPokemon> {
        val jsonObject = json.asJsonObject
        val jsonArray = jsonObject.get("pokedex").asJsonArray

        val pokedexData = LinkedHashMap<Int, ExternalPokemon>()

        for (pokedexJsonElement in jsonArray) {

            val pokedexJsonObject = pokedexJsonElement as JsonObject

            val id = pokedexJsonObject.get("id").asInt
            val number = pokedexJsonObject.get("num").asInt
            val name = pokedexJsonObject.get("species").asString
            val gender = pokedexJsonObject.get("gender").asInt

            val types = IntArray(2)
            val jsonTypes = pokedexJsonObject.get("types").asJsonArray
            for (i in 0 until jsonTypes.size())
                types[i] = jsonTypes.get(i).asInt

            val abilities = IntArray(3)
            val jsonAbilities = pokedexJsonObject.get("abilities").asJsonObject
            abilities[0] = jsonAbilities.get("0").asInt
            abilities[1] = jsonAbilities.get("1").asInt
            abilities[2] = jsonAbilities.get("H").asInt

            val eggGroups = IntArray(2)
            val jsonEggGroups = pokedexJsonObject.get("eggGroups").asJsonArray
            for (i in 0 until jsonEggGroups.size())
                eggGroups[i] = jsonEggGroups.get(i).asInt

            val evolutionChain = pokedexJsonObject.get("evolution_chain").asInt

            val previousEvolution: Int
            if (pokedexJsonElement.has("prevo"))
                previousEvolution = pokedexJsonObject.get("prevo").asInt
            else
                previousEvolution = 0

            val evolutions = ArrayList<Int>(3)
            if (pokedexJsonObject.has("evos")) {
                val jsonEvolutions = pokedexJsonObject.get("evos").asJsonArray
                for (evolution in jsonEvolutions)
                    evolutions.add(evolution.asInt)
            } else
                evolutions.add(0)

            val stats = IntArray(6)
            val jsonStats = pokedexJsonObject.get("baseStats").asJsonArray
            for (i in 0 until jsonStats.size())
                stats[i] = jsonStats.get(i).asInt


            val deserializedPokedexBlock = ExternalPokemon(
                id, number, name, gender, types, abilities, eggGroups, evolutionChain,
                previousEvolution, evolutions, stats
            )
            pokedexData[deserializedPokedexBlock.id] = deserializedPokedexBlock

        }

        return pokedexData
    }
}