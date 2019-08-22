package net.ntworld.foundation.internal

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

internal class EncryptedInfo {
    companion object {
        const val STORED_KEY = "__encryptedInformation"
    }

    @Serializable
    private data class Item(
        val cipherId: String,
        val algorithm: String,
        val fields: MutableList<String>
    )

    internal data class EncryptorInfo(
        val cipherId: String,
        val algorithm: String
    )

    private var list = mutableListOf<Item>()

    fun markAsEncrypted(field: String, cipherId: String, algorithm: String) {
        for (item in list) {
            if (item.cipherId == cipherId && algorithm == algorithm && !item.fields.contains(field)) {
                item.fields.add(field)
                return
            }
        }

        list.add(
            Item(
                cipherId = cipherId,
                algorithm = algorithm,
                fields = mutableListOf(field)
            )
        )
    }

    fun findEncryptorInfo(field: String): EncryptorInfo? {
        for (item in list) {
            if (item.fields.contains(field)) {
                return EncryptorInfo(cipherId = item.cipherId, algorithm = item.algorithm)
            }
        }
        return null
    }

    fun serialize(json: Json): String {
        return json.stringify(Item.serializer().list, list)
    }

    fun deserialize(json: Json, input: String) {
        val parsed = json.parse(Item.serializer().list, input)

        list.clear()
        list.addAll(parsed)
    }
}