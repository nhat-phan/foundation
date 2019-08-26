package net.ntworld.foundation.internal

import kotlinx.serialization.Serializable

@Serializable
internal class SerializableMessage(
    val id: String?,
    val type: String?,
    val body: String,
    val attributes: Map<String, SerializableMessageAttribute>
)