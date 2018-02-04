package marcelo.breguenait.breedingassistant.data.internal

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class JsonInternalPokemonDeserializer : JsonDeserializer<ArrayList<InternalPokemon>> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ArrayList<InternalPokemon> {
        val jsonArray = json?.asJsonArray

        val pokemons = ArrayList<InternalPokemon>()


        if (jsonArray != null) {
            jsonArray.map { it as JsonObject }
                    .forEach {

                        //TODO: check validity
                        val pokemon = InternalPokemon(it.get("externalId").asInt, it.get("dateCreated").asLong, it.get("internalId").asString)
                        pokemon.abilitySlot = it.get("abilitySlot").asInt

                        if (it.has("gender")) pokemon.gender = it.get("gender").asInt
                        else pokemon.gender = it.get("internalGender").asInt

                        val jsonIVs = it.get("IVs").asJsonArray

                        for (i in pokemon.IVs.indices)
                            pokemon.IVs[i] = jsonIVs.get(i).asInt

                        pokemon.natureId = it.get("natureId").asInt

                        pokemons.add(pokemon)

                    }
        }
        return pokemons
    }
}