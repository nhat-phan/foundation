package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata

@Serializable
data class RequestHandlerSetting(
    val request: ClassInfo,
    override val version: Int,
    override val handler: ClassInfo,
    override val metadata: KotlinMetadata,
    override val makeByFactory: Boolean
) : VersionedHandlerSetting {
    override val name: String = "${handler.packageName}.${handler.className}"
}