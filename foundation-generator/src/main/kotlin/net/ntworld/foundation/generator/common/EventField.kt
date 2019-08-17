package net.ntworld.foundation.generator.common

data class EventField(
    val name: String,
    val encrypted: Boolean,
    val faked: Boolean,
    val fakedType: String
)