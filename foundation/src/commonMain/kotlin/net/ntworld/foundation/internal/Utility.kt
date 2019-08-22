package net.ntworld.foundation.internal

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal object Utility {
    fun serializeMap(json: Json, data: Map<String, String>): String {
        val jsonObject = JsonObject(data.mapValues {
            JsonPrimitive(it.value)
        })
        return json.stringify(JsonObject.serializer(), jsonObject)
    }

    fun deserializeMap(json: Json, input: String): Map<String, String> {
        val jsonObject = json.parse(JsonObject.serializer(), input)
        val map = mutableMapOf<String, String>()
        jsonObject.forEach {
            val content = it.value.primitive.contentOrNull
            if (null !== content) {
                map[it.key] = content
            }
        }
        return map
    }
}

//fun main() {
//    val map = mapOf(
//        "a" to "\"test\"",
//        "b" to 123,
//        "c" to 1.01,
//        "d" to 'a'
//    )
//
//    val json = Json(JsonConfiguration.Stable)
////    val jsonObject = JsonObject(data.mapValues {
////        JsonPrimitive(it.value)
////    })
////    return json.stringify(JsonObject.serializer(), jsonObject)
//}