package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.common.ClassInfo
import net.ntworld.foundation.generator.common.EventField

data class EventSettings(
    val event: ClassInfo,
    val fields: EventField
)