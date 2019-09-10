package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import net.ntworld.foundation.generator.type.ClassInfo

object ContractOnlyModeGenerator {
    fun generate(settingsClass: String?, settings: GeneratorSettings): GeneratedFile {
        if (null === settingsClass || settingsClass.isEmpty()) {
            throw Exception("Please provide valid \"foundation.processor.settings-class\" options")
        }
        val target = Utility.stringToClassInfo(settingsClass)
        val file = buildFile(target, settings)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeMainFile(target, stringBuffer.toString())
    }

    private fun buildFile(target: ClassInfo, settings: GeneratorSettings): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        val type = TypeSpec.objectBuilder(target.toClassName())

        val value = GeneratorSettings.stringify(settings, false)
        type.addProperty(
            PropertySpec.builder("settings", String::class)
                .addModifiers(KModifier.CONST)
                .initializer("%S", value)
                .build()
        )

        return file.addType(type.build()).build()
    }
}