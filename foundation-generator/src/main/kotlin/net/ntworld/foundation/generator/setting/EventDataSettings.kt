package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo

data class EventDataSettings(
    val event: ClassInfo,
    val type: String,
    val variant: Int
)
