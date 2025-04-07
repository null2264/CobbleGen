import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

val prettyJson = Json { prettyPrint = true }
@OptIn(ExperimentalSerializationApi::class)
val lenientJson = Json {
    ignoreUnknownKeys = true
    allowComments = true
    allowTrailingComma = true
}
fun MutableList<JsonElement>.addJson(value: String) {
    add(JsonPrimitive(value))
}
