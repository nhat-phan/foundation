package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo

data class FactorySettings(
    val aggregate: ClassInfo,
    val base: ClassInfo,
    val isAbstract: Boolean
)