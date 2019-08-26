package net.ntworld.foundation

import kotlinx.io.ByteBuffer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.ntworld.foundation.internal.*
import net.ntworld.foundation.internal.MESSAGE_DATA_TYPE_STRING
import net.ntworld.foundation.internal.MessageAttributeImpl
import net.ntworld.foundation.internal.MessageImpl
import net.ntworld.foundation.internal.SerializableMessage

object MessageUtility {
    private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

    fun createMessage(body: String): Message = MessageImpl(
        id = null,
        type = null,
        body = body,
        attributes = mapOf()
    )

    fun createMessage(body: String, attributes: Map<String, MessageAttribute>): Message = MessageImpl(
        id = null,
        type = null,
        body = body,
        attributes = attributes
    )

    fun createAttribute(dataType: String, stringValue: String): MessageAttribute = MessageAttributeImpl(
        dataType = dataType,
        stringValue = stringValue,
        binaryValue = null
    )


    fun createAttribute(dataType: String, binaryValue: ByteBuffer): MessageAttribute = MessageAttributeImpl(
        dataType = dataType,
        stringValue = null,
        binaryValue = binaryValue
    )

    fun createStringAttribute(value: String): MessageAttribute = MessageAttributeImpl(
        dataType = MESSAGE_DATA_TYPE_STRING,
        stringValue = value,
        binaryValue = null
    )

    fun createBinaryAttribute(value: ByteBuffer): MessageAttribute = MessageAttributeImpl(
        dataType = MESSAGE_DATA_TYPE_STRING,
        stringValue = null,
        binaryValue = value
    )

    fun serialize(message: Message): String {
        val attributes = message.attributes.mapValues {
            val binaryValue = it.value.binaryValue
            if (null !== binaryValue) {
                return@mapValues SerializableMessageAttribute(
                    type = it.value.dataType,
                    value = Base64.encode(binaryValue.array()),
                    binary = true
                )
            }

            val stringValue = it.value.stringValue
            if (null !== stringValue) {
                return@mapValues SerializableMessageAttribute(
                    type = it.value.dataType,
                    value = stringValue,
                    binary = false
                )
            }
            throw Exception("Invalid message attribute, the stringValue and binaryValue are null")
        }
        return json.stringify(
            SerializableMessage.serializer(), SerializableMessage(
                id = message.id,
                type = message.type,
                body = message.body,
                attributes = attributes
            )
        )
    }

    fun deserialize(input: String): Message {
        val serializableMessage = json.parse(SerializableMessage.serializer(), input)
        val attributes = serializableMessage.attributes.mapValues {
            if (it.value.binary) {
                val bytes = Base64.decode(it.value.value)
                val byteBuffer = ByteBuffer.allocate(bytes.size)
                byteBuffer.put(bytes)
                this.createAttribute(it.value.type, byteBuffer)
            } else {
                this.createAttribute(it.value.type, it.value.value)
            }
        }

        return MessageImpl(
            id = serializableMessage.id,
            type = serializableMessage.type,
            body = serializableMessage.body,
            attributes = attributes
        )
    }
}
