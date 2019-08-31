package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo

interface HandlerSetting: Setting {
    val handler: ClassInfo

    val makeByFactory: Boolean
}