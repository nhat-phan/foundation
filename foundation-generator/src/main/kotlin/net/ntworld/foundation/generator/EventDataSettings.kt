package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.common.ClassInfo

data class EventDataSettings(
    val event: ClassInfo,
    val type: String,
    val variant: Int,
    val target: ClassInfo? = null,
    val converterTarget: ClassInfo? = null
)
