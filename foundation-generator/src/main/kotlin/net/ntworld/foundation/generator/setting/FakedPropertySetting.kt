package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo

@Serializable
data class FakedPropertySetting(
    val contract: ClassInfo,
    val property: String,
    val fakedType: String
) : Setting {
    override val name: String = "${contract.fullName()}\$$property"
}
