package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo

object ContractImplementationFactoryGenerator {
    fun generate(
        setting: ContractSetting,
        properties: Map<String, ContractReader.Property>,
        implementation: ClassInfo
    ): GeneratedFile {
        val target = Utility.findContractImplementationFactoryTarget(setting)
        val file = buildFile(target, setting, properties, implementation)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    private fun buildFile(
        target: ClassInfo,
        setting: ContractSetting,
        properties: Map<String, ContractReader.Property>,
        implementation: ClassInfo
    ): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(target, setting, properties, implementation))

        return file.build()
    }

    private fun buildClass(
        target: ClassInfo,
        setting: ContractSetting,
        properties: Map<String, ContractReader.Property>,
        implementation: ClassInfo
    ): TypeSpec {
        val type = TypeSpec.objectBuilder(target.className)
        val make = FunSpec.builder("make")
        make.returns(setting.contract.toClassName())

        properties.forEach { (name, property) ->
            make.addParameter(name, property.type)
        }

        val code = CodeBlock.builder()
        code.add("return %T(\n", implementation.toClassName())
        code.indent()

        val lastIndex = properties.values.size - 1
        properties.values.forEachIndexed { index, property ->
            code.add("%L = %L", property.name, property.name)
            if (index != lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }

        code.unindent()
        code.add(")\n")

        make.addCode(code.build())
        type.addFunction(make.build())
        return type.build()
    }
}