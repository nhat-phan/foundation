package net.ntworld.foundation.generator.type

data class EventField(
    val name: String,
    val metadata: Boolean,
    val encrypted: Boolean,
    val faked: String
)