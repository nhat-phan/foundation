package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.util.ContractReader

class ContractFactoryTestGenerator(private val platform: Platform) {
    data class Item(
        val contract: ClassInfo,
        val implementation: ClassInfo
    )

    private val items = mutableMapOf<String, Item>()

    fun add(contract: ClassInfo, implementation: ClassInfo) {
        items[contract.fullName()] = Item(
            contract = contract,
            implementation = implementation
        )
    }

    fun generate(settings: GeneratorSettings, namespace: String? = null): GeneratedFile {
        val target = Utility.findContractFactoryTarget(
            items.map { it.value.contract },
            namespace
        )
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeTestFile(target, stringBuffer.toString())
    }

    private fun buildFile(settings: GeneratorSettings, target: ClassInfo): FileSpec {
        val allSettings = settings.toMutable()
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)

        file.addProperty(buildFakerProperty())
        file.addFunction(buildCreateFakedDataFunction())
        val reader = ContractReader(settings.contracts, settings.fakedAnnotations)
        items.forEach { (contract, item) ->
            if (!reader.hasCompanionObject(contract)) {
                return@forEach
            }
            val setting = allSettings.getContract(contract)
            val properties = reader.findPropertiesOfContract(contract)
            if (null !== setting && null !== properties) {
                ContractExtensionTestGenerator.generate(setting, properties, item.implementation, file)
            }
        }
        return file.build()
    }

    private fun buildFakerProperty(): PropertySpec {
        val code = when (platform) {
            Platform.Jvm -> getInitFakerForJvm()
        }

        return PropertySpec.builder("faker", Framework.Faker)
            .addModifiers(KModifier.PRIVATE)
            .initializer(code)
            .build()
    }

    private fun getInitFakerForJvm(): CodeBlock {
        val code = CodeBlock.builder()
        code.add("%T(%T())\n", Framework.JavaFakerWrapper, Framework.JavaFaker)

        return code.build()
    }

    private fun buildCreateFakedDataFunction(): FunSpec {
        return FunSpec.builder("createFakedData")
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
    }
}