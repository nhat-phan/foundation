package net.ntworld.foundation.generator.type

import kotlinx.serialization.Serializable

@Serializable
data class EventField(
    val name: String,
    val metadata: Boolean,
    val encrypted: Boolean,
    val faked: String
)