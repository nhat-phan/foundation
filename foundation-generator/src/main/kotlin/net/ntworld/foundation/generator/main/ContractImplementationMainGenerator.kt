package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.ContractReader
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo

object ContractImplementationMainGenerator {
    fun generate(setting: ContractSetting, properties: Map<String, ContractReader.Property>): GeneratedFile {
        val target = Utility.findContractImplementationTarget(setting)
        val file =
            buildFile(setting, target, properties)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildMainGeneratedFile(target, stringBuffer.toString())
    }

    private fun buildFile(
        setting: ContractSetting,
        target: ClassInfo,
        properties: Map<String, ContractReader.Property>
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
        properties: Map<String, ContractReader.Property>
    ): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
        type.addSuperinterface(setting.contract.toClassName())

        if (properties.isNotEmpty())
            type.addModifiers(KModifier.DATA)

        val primaryConstructor = FunSpec.constructorBuilder()

        properties.forEach { (name, property) ->
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