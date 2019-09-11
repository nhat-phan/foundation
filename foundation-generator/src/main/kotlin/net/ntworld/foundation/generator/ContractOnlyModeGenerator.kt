package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import net.ntworld.foundation.generator.type.ClassInfo
import java.lang.StringBuilder

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

        val value = makePrettyBase64(GeneratorSettings.toBase64String(settings))
        type.addProperty(
            PropertySpec.builder("settings", String::class)
                .addModifiers(KModifier.CONST)
                .initializer("\"\"\"%L\"\"\"", value)
                .build()
        )

        return file.addType(type.build()).build()
    }

    private fun makePrettyBase64(input: String, firstLineLimit: Int = 49, columnLimit: Int = 77): String {
        val list = mutableListOf(
            StringBuilder()
        )
        var count = 1
        for (i in 0 until input.length) {
            val shouldBreak = i == firstLineLimit || count == columnLimit

            if (!shouldBreak) {
                list[list.lastIndex].append(input[i])
                count++
                continue
            }

            count = 1
            list.add(StringBuilder())
            list[list.lastIndex].append(input[i])
        }
        val result = StringBuilder()
        for (item in list) {
            result.append(item.toString())
            result.appendln()
        }
        return result.toString()
    }
}