package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.Property

object ContractImplementationMainGenerator {
    fun findImplementationTarget(setting: ContractSetting) = Utility.findContractImplementationTarget(setting)

    fun generate(setting: ContractSetting, properties: Map<String, Property>): GeneratedFile {
        val target = findImplementationTarget(setting)
        val file =
            buildFile(setting, target, properties)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeMainFile(target, stringBuffer.toString())
    }

    private fun buildFile(
        setting: ContractSetting,
        target: ClassInfo,
        properties: Map<String, Property>
    ): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(
            buildClass(
                setting,
                target,
                properties
            )
        )

        return file.build()
    }

    private fun buildClass(
        setting: ContractSetting,
        target: ClassInfo,
        properties: Map<String, Property>
    ): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
            .addAnnotation(
                AnnotationSpec.builder(Serializable::class)
                    .build()
            )
        type.addSuperinterface(setting.contract.toClassName())

        if (properties.isNotEmpty())
            type.addModifiers(KModifier.DATA)

        val primaryConstructor = FunSpec.constructorBuilder()

        properties.forEach { (name, property) ->
            if (property.hasBody) {
                return@forEach
            }

            primaryConstructor
                .addParameter(name, property.type)

            type.addProperty(
                PropertySpec.builder(name, property.type)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer(name)
                    .build()
            )
        }

        type.primaryConstructor(primaryConstructor.build())
        return type.build()
    }
}