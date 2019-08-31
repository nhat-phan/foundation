package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo

@Serializable
data class EventHandlerSetting(
    val events: List<ClassInfo>,
    override val handler: ClassInfo,
    override val makeByFactory: Boolean
) : HandlerSetting {
    override val name: String
        get() = "${handler.packageName}.${handler.className}"
}