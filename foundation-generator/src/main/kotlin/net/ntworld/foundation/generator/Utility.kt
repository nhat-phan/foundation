package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo

object Utility {
    fun findEventConverterTarget(settings: EventSettings): ClassInfo {
        return ClassInfo(
            className = "${settings.event.className}Converter",
            packageName = findTargetNamespace(settings.event.packageName)
        )
    }

    fun findEventDataMessageConverterTarget(settings: EventSettings): ClassInfo {
        return ClassInfo(
            className = "${settings.event.className}DataMessageConverter",
            packageName = findTargetNamespace(settings.event.packageName)
        )
    }

    fun findEventDataTarget(settings: EventSettings): ClassInfo {
        return ClassInfo(
            className = "${settings.event.className}Data",
            packageName = findTargetNamespace(settings.event.packageName)
        )
    }

    private fun findTargetNamespace(input: String): String {
        return "$input.generated"
    }
}