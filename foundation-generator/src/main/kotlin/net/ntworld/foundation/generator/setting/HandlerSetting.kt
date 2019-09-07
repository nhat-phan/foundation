package net.ntworld.foundation.generator.setting

import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata

interface HandlerSetting: Setting {
    val handler: ClassInfo

    val metadata: KotlinMetadata

    val makeByFactory: Boolean
}