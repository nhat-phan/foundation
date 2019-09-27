package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo

@Serializable
data class MessagingSetting(
    val contract: ClassInfo,
    val channel: String,
    val type: String
): Setting {
    override val name: String = contract.fullName()
}
