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

    fun generate(settings: GeneratorSettings, utilityTarget: ClassInfo, namespace: String? = null): GeneratedFile {
        val target = Utility.findContractFactoryTargetForTest(
            items.map { it.value.contract },
            namespace
        )
        val file = buildFile(settings, utilityTarget, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeTestFile(target, stringBuffer.toString())
    }

    private fun buildFile(settings: GeneratorSettings, utilityTarget: ClassInfo, target: ClassInfo): FileSpec {
        val allSettings = settings.toMutable()
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)

        file.addFunction(buildCreateFakedDataFunction(utilityTarget))
        val reader = ContractReader(settings.contracts, settings.fakedAnnotations, settings.fakedProperties)
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

    private fun buildCreateFakedDataFunction(utilityTarget: ClassInfo): FunSpec {
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
            .addCode("return %T.faker.makeFakeData(type) as T\n", utilityTarget.toClassName())
            .build()
    }
}