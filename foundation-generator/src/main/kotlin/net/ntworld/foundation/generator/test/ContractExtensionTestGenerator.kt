package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.Property

internal object ContractExtensionTestGenerator {
    fun generate(
        setting: ContractSetting,
        properties: Map<String, Property>,
        implementation: ClassInfo,
        file: FileSpec.Builder
    ) {
        val propertiesList = properties.filter { !it.value.hasBody }.values.toList()
        val requiredProperties = (propertiesList.filter { it.fakedType.isEmpty() }).toMutableList()
        val optionalProperties = (propertiesList.filter { it.fakedType.isNotEmpty() }).toMutableList()
        while (optionalProperties.isNotEmpty()) {
            buildMakeFunction(setting, requiredProperties, optionalProperties, implementation, file)
            requiredProperties.add(optionalProperties.removeAt(0))
        }
    }

    private fun buildMakeFunction(
        setting: ContractSetting,
        requiredProperties: List<Property>,
        optionalProperties: List<Property>,
        implementation: ClassInfo,
        file: FileSpec.Builder
    ) {
        val make = FunSpec.builder("make")
            .receiver(setting.contract.toCompanionClassName())
            .returns(setting.contract.toClassName())

        requiredProperties.forEach {
            make.addParameter(it.name, it.type)
        }

        val code = CodeBlock.builder()
        code.add("return %T(\n", implementation.toClassName())
        code.indent()

        requiredProperties.forEachIndexed { index, property ->
            code.add("%L = %L", property.name, property.name)
            if (index != requiredProperties.lastIndex || optionalProperties.isNotEmpty()) {
                code.add(",")
            }
            code.add("\n")
        }

        optionalProperties.forEachIndexed { index, item ->
            code.add("%L = createFakedData(%S)", item.name, item.fakedType)
            if (index != optionalProperties.lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }

        code.unindent()
        code.add(")\n")

        make.addCode(code.build())
        file.addFunction(make.build())
    }
}