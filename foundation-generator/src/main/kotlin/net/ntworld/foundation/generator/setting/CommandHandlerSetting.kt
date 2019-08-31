package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo

data class CommandHandlerSetting(
    val command: ClassInfo,
    val version: Int,
    override val handler: ClassInfo,
    override val makeByFactory: Boolean
) : HandlerSetting {
    override val name: String
        get() = "${handler.packageName}.${handler.className}"
}