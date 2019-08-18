package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo

internal object Utility {
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

    fun buildGeneratedFile(target: ClassInfo, content: String): GeneratedFile {
        val directory = target.packageName.replace(".", "/")
        val fileName = target.className + ".kt"
        return GeneratedFile(
            directory = "/$directory",
            fileName = fileName,
            path = "/$directory/$fileName",
            content = content
        )
    }

    private fun findTargetNamespace(input: String): String {
        return "$input.generated"
    }
}