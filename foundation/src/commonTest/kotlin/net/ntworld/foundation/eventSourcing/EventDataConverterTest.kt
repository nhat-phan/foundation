package net.ntworld.foundation.eventSourcing

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.test.Test

class EventDataConverterTest {
    internal object FakedEncryptor : Encryptor {
        override val cipherId: String = "secret-id"
        override val algorithm: String = "faked"

        override fun encrypt(input: String): String {
            return input.toUpperCase()
        }

        override fun decrypt(input: String): String {
            return input.toLowerCase()
        }
    }

    @Serializable
    private data class TestEvent(
        val id: String,
        val byte: Byte,
        val short: Short,
        val int: Int,
        val long: Long
    )

    @Test
    fun testWrite() {
        val event = TestEvent(
            id = "id",
            byte = 100,
            short = 9999,
            int = 123456,
            long = 1234567890
        )
        val json = Json(JsonConfiguration.Stable)

        // only this one => the Event should be serializable
        val string = json.stringify(TestEvent.serializer(), event)
        val jsonObject = json.parse(JsonObject.serializer(), string)

        val dataMap = mutableMapOf<String, JsonElement>()
        val encryptedMap = mutableMapOf<String, JsonElement>()
        val metadataMap = mutableMapOf<String, JsonElement>()
        jsonObject.forEach {
            when (it.key) {
                "id" -> dataMap[it.key] = it.value
                "byte" -> encryptedMap[it.key] = it.value
                "short" -> metadataMap[it.key] = it.value
                "int" -> encryptedMap[it.key] = it.value
                "long" -> metadataMap[it.key] = it.value
            }
        }
        val encryptedText = FakedEncryptor.encrypt(
            json.stringify(JsonObject.serializer(), JsonObject(encryptedMap))
        )
        dataMap["__encrypted@cipherId"] = JsonPrimitive(FakedEncryptor.cipherId)
        dataMap["__encrypted@algorithm"] = JsonPrimitive(FakedEncryptor.algorithm)
        dataMap["__encrypted@text"] = JsonPrimitive(encryptedText)

        val data = json.stringify(JsonObject.serializer(), JsonObject(dataMap))
        val metadata = json.stringify(JsonObject.serializer(), JsonObject(metadataMap))

        read(data, metadata)
    }

    fun read(data: String, metadata: String) {
        val json = Json(JsonConfiguration.Stable)

        val dataObject = json.parse(JsonObject.serializer(), data)
        val metadataObject = json.parse(JsonObject.serializer(), metadata)
        val rawMap = mutableMapOf<String, JsonElement>()

        metadataObject.forEach { rawMap[it.key] = it.value }
        var hasEncryptedFields = false
        dataObject.forEach {
            if (!it.key.startsWith("__encrypted@")) {
                rawMap[it.key] = it.value
            } else {
                hasEncryptedFields = true
            }
        }
        if (hasEncryptedFields) {
            val text = dataObject["__encrypted@text"]
            val plainText = FakedEncryptor.decrypt(text!!.content)
            val encryptedMap = json.parse(JsonObject.serializer(), plainText)
            encryptedMap.forEach { rawMap[it.key] = it.value }
        }

        val raw = json.stringify(JsonObject.serializer(), JsonObject(rawMap))

        // only this one => the Event should be serializable
        val event = json.parse(TestEvent.serializer(), raw)
        println(event.long::class)
    }
}