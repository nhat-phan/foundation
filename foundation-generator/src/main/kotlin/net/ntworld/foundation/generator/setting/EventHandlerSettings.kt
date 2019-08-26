package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.HandlerSettings

data class EventHandlerSettings(
    val event: ClassInfo,
    override val bus: String,
    override val handler: ClassInfo,
    override val isResolvable: Boolean
) : HandlerSettings