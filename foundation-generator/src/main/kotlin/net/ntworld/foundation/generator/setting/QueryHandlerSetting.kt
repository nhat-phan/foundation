package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo

data class QueryHandlerSetting(
    val query: ClassInfo,
    val version: Int,
    override val bus: String,
    override val handler: ClassInfo,
    override val makeByFactory: Boolean
) : HandlerSetting