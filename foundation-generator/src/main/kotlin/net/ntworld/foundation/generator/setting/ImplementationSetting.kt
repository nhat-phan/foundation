package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.ComponentType

@Serializable
data class ImplementationSetting(
    val implementation: ClassInfo,
    val contract: ClassInfo,
    val type: ComponentType,
    val isGenerated: Boolean
) : Setting {
    override val name: String = implementation.fullName()
}
