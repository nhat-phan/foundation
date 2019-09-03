package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo

@Serializable
data class FakedAnnotationSetting(
    val annotation: ClassInfo,
    val fakedType: String
) : Setting {
    override val name: String = "${annotation.packageName}.${annotation.className}"
}