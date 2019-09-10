package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.FileSpec
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.util.ContractReader

class ContractFactoryMainGenerator {
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

        return GeneratedFile.makeMainFile(target, stringBuffer.toString())
    }

    private fun buildFile(settings: GeneratorSettings, target: ClassInfo): FileSpec {
        val allSettings = settings.toMutable()
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        val reader = ContractReader(settings.contracts, settings.fakedAnnotations, settings.fakedProperties)
        items.forEach { (contract, item) ->
            if (!reader.hasCompanionObject(contract)) {
                return@forEach
            }
            val setting = allSettings.getContract(contract)
            val properties = reader.findPropertiesOfContract(contract)
            if (null !== setting && null !== properties) {
                ContractExtensionMainGenerator.generate(setting, properties, item.implementation, file)
            }
        }
        return file.build()
    }
}