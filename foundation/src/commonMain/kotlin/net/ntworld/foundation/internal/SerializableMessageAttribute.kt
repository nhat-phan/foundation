package net.ntworld.foundation.internal

import kotlinx.serialization.Serializable

@Serializable
data class SerializableMessageAttribute(
    val type: String,
    val value: String,
    val binary: Boolean
)