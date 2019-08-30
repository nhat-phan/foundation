package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo

data class EventHandlerSetting(
    val event: ClassInfo,
    override val bus: String,
    override val handler: ClassInfo,
    override val makeByFactory: Boolean
) : HandlerSetting