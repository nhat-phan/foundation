package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo

@Deprecated("No longer needed", level = DeprecationLevel.WARNING)
object ContractImplementationFactoryTestGenerator {
    fun generate(
        setting: ContractSetting,
        properties: Map<String, ContractReader.Property>,
        implementation: ClassInfo
    ): GeneratedFile {
        val target = Utility.findContractImplementationFactoryTarget(setting)
        val file = buildFile(
            target,
            setting,
            properties,
            implementation
        )
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildTestGeneratedFile(target, stringBuffer.toString())
    }

    private fun buildFile(
        target: ClassInfo,
        setting: ContractSetting,
        properties: Map<String, ContractReader.Property>,
        implementation: ClassInfo
    ): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(
            buildClass(
                target,
                setting,
                properties,
                implementation
            )
        )

        return file.build()
    }

    private fun buildClass(
        target: ClassInfo,
        setting: ContractSetting,
        properties: Map<String, ContractReader.Property>,
        implementation: ClassInfo
    ): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
        type.primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("faker", Framework.Faker)
                .build()
        )
        type.addProperty(
            PropertySpec.builder("faker", Framework.Faker)
                .addModifiers(KModifier.PRIVATE)
                .initializer("faker")
                .build()
        )
        val propertiesList = properties.values.toList()
        val requiredProperties = (propertiesList.filter { it.fakedType.isEmpty() }).toMutableList()
        val optionalProperties = (propertiesList.filter { it.fakedType.isNotEmpty() }).toMutableList()

        while (optionalProperties.isNotEmpty()) {
            buildMakeFunction(type, setting.contract, implementation, requiredProperties, optionalProperties)
            requiredProperties.add(optionalProperties.removeAt(0))
        }
        buildMakeFunction(type, setting.contract, implementation, propertiesList, listOf())

        buildCreateFakedDataFunction(type)
        return type.build()
    }

    private fun buildMakeFunction(
        type: TypeSpec.Builder,
        contract: ClassInfo,
        implementation: ClassInfo,
        requiredProperties: List<ContractReader.Property>,
        optionalProperties: List<ContractReader.Property>
    ) {
        val make = FunSpec.builder("make")
        make.returns(contract.toClassName())

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
            code.add(
                "%L = createFakedData(%S)",
                item.name,
                item.fakedType
            )

            if (index != optionalProperties.lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }

        code.unindent()
        code.add(")\n")

        make.addCode(code.build())
        type.addFunction(make.build())
    }

    private fun buildCreateFakedDataFunction(type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("createFakedData")
                .addModifiers(KModifier.PRIVATE)
                .addTypeVariable(TypeVariableName.invoke("T"))
                .addParameter("type", String::class)
                .returns(TypeVariableName.invoke("T"))
                .addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "UNCHECKED_CAST")
                        .build()
                )
                .addCode("return faker.makeFakeData(type) as T\n")
                .build()
        )
    }
}