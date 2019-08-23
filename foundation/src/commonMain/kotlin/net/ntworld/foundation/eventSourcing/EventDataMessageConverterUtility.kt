package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Message
import net.ntworld.foundation.internal.MessageImpl

object EventDataMessageConverterUtility {
    const val MESSAGE_BODY_TYPE = "EventMessage"

    fun canConvert(message: Message, type: String, variant: Int): Boolean {
        return message.attributes["dataType"] == MESSAGE_BODY_TYPE &&
            message.attributes["eventType"] == "$type:$variant"
    }

    fun toMessage(body: String, type: String, variant: Int): Message {
        return MessageImpl(
            id = null,
            type = null,
            body = body,
            attributes = mapOf(
                "dataType" to MESSAGE_BODY_TYPE,
                "eventType" to "$type:$variant"
            )
        )
    }
}