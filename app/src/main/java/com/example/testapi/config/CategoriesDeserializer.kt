import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CategoriesDeserializer : JsonDeserializer<List<String>> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<String> {
        val categories = mutableListOf<String>()
        if (json.isJsonArray) {
            json.asJsonArray.forEach {
                categories.add(it.asString)
            }
        } else if (json.isJsonPrimitive) {
            categories.add(json.asString)
        }
        return categories
    }
}
