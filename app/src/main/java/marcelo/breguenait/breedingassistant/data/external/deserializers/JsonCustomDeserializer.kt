package marcelo.breguenait.breedingassistant.data.external.deserializers

import com.google.gson.JsonDeserializer
import java.lang.reflect.Type

/**
 * Created by Marcelo on 14/01/2018.
 */
interface JsonCustomDeserializer<T> : JsonDeserializer<T> {
    val type: Type
}