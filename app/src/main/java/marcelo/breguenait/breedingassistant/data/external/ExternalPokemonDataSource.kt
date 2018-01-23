package marcelo.breguenait.breedingassistant.data.external

import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalAbility
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import marcelo.breguenait.breedingassistant.data.external.deserializers.JsonAbilityDeserializer
import marcelo.breguenait.breedingassistant.data.external.deserializers.JsonCustomDeserializer
import marcelo.breguenait.breedingassistant.data.external.deserializers.JsonNatureDeserializer
import marcelo.breguenait.breedingassistant.data.external.deserializers.JsonPokedexDeserializer
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Created by Marcelo on 14/01/2018.
 */
class ExternalPokemonDataSource(private val assetManager: AssetManager) {

    fun <T> loadExternalAbilities(): LinkedHashMap<Int, ExternalAbility> {
        return openWithCustomDeserializer(JsonAbilityDeserializer(), "abilities.json")
    }

    fun <T> loadExternalPokemons(): LinkedHashMap<Int, ExternalPokemon> {
        return openWithCustomDeserializer(JsonPokedexDeserializer(), "pokedex.json")
    }

    fun <T> loadExternalNatures(): LinkedHashMap<Int, ExternalNature> {
        return openWithCustomDeserializer(JsonNatureDeserializer(), "natures.json")
    }


    private inline fun <reified T> openWithCustomDeserializer(
        deserializer: JsonCustomDeserializer<*>,
        fileName: String
    ): T {
        val gsonBuilder = GsonBuilder()
        val customGson: Gson
        val assetFile: InputStream
        val reader: InputStreamReader

        val structureType = deserializer.type
        gsonBuilder.registerTypeAdapter(structureType, deserializer)
        customGson = gsonBuilder.create()

        try {
            assetFile = assetManager.open(fileName)
            reader = InputStreamReader(assetFile, "UTF-8")
            return customGson.fromJson(reader, structureType)
        } catch (e: IOException) {
            e.printStackTrace()
            throw Exception()
        }

    }
}