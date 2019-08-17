package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField

data class EventSettings(
    val event: ClassInfo,
    val fields: List<EventField>
)