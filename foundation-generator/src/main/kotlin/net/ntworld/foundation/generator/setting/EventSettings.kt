package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField

@Serializable
data class EventSettings(
    val event: ClassInfo,
    val fields: List<EventField>,
    val type: String,
    val variant: Int
)