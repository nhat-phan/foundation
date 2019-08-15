package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.common.ClassInfo

data class EventDataGeneratorSettings(
    val event: ClassInfo,
    val type: String,
    val variant: Int,
    val target: ClassInfo? = null
)
