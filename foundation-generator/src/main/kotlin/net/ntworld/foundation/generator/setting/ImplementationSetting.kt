package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo

data class ImplementationSetting(
    val implementation: ClassInfo,
    val contract: ClassInfo,
    val type: Type
) {
    enum class Type {
        Event,
        Command,
        Query,
        Aggregate,
        Unknown
    }
}
