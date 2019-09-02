package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo

@Serializable
data class MessageSetting(
    val contract: ClassInfo,
    val channel: String,
    val type: Type
): Setting {
    override val name: String = "${contract.packageName}.${contract.className}"

    enum class Type {
        Event,
        Command,
        Query
    }
}
