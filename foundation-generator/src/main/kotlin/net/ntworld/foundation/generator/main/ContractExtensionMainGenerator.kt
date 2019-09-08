package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.Property

internal object ContractExtensionMainGenerator {
    fun generate(
        setting: ContractSetting,
        properties: Map<String, Property>,
        implementation: ClassInfo,
        file: FileSpec.Builder
    ) {
        val make = FunSpec.builder("make")
        make
            .receiver(setting.contract.toCompanionClassName())
            .returns(setting.contract.toClassName())

        val notImplementedProperties = properties.filter { !it.value.hasBody }

        notImplementedProperties.forEach { (name, property) ->
            make.addParameter(name, property.type)
        }

        val code = CodeBlock.builder()
        code.add("return %T(\n", implementation.toClassName())
        code.indent()

        val lastIndex = notImplementedProperties.values.size - 1
        notImplementedProperties.values.forEachIndexed { index, property ->
            code.add("%L = %L", property.name, property.name)
            if (index != lastIndex) {
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