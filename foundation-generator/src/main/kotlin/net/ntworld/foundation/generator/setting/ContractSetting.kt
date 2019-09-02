package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.ContractProperty
import net.ntworld.foundation.generator.type.KotlinMetadata

@Serializable
data class ContractSetting(
    val contract: ClassInfo,
    val metadata: KotlinMetadata,
    val supertypes: List<String>?,
    val properties: Map<String, ContractProperty>
) : Setting {
    override val name: String = "${contract.packageName}.${contract.className}"
}