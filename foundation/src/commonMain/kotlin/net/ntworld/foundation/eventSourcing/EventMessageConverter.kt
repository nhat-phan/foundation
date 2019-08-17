package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Message
import net.ntworld.foundation.MessageConverter
import net.ntworld.foundation.internal.MessageImpl

object EventMessageConverter {
    const val MESSAGE_TYPE = "EventMessage"

    fun canConvert(message: Message, type: String, variant: Int): Boolean {
        return message.type == MESSAGE_TYPE && message.attributes["type"] == type && message.attributes["variant"] == variant
    }

    fun toMessage(input: EventData): Message {
        val attributes = mapOf(
            "id" to input.id,
            "type" to input.type,
            "variant" to input.variant,
            "streamId" to input.streamId,
            "streamType" to input.streamType,
            "version" to input.version,
            "data" to input.data,
            "metadata" to input.metadata
        )
        return MessageImpl(id = null, type = MESSAGE_TYPE, attributes = attributes)
    }
}