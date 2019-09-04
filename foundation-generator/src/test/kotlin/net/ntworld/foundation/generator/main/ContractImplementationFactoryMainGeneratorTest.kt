package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.ContractReader
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractImplementationFactoryMainGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate BasicTypesContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.basic-types.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.BasicTypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationFactoryMainGenerator.generate(
            setting!!,
            properties!!,
            ClassInfo(
                packageName = "${setting.contract.packageName}.generated",
                className = "${setting.contract.className}Impl"
            )
        )
        assertGeneratedFileMatched(result, "/ContractImplementationFactory/BasicTypesContract.txt")
    }

    @Test
    fun `testGenerate ListTypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.list-type.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.ListTypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationFactoryMainGenerator.generate(
            setting!!,
            properties!!,
            ClassInfo(
                packageName = "${setting.contract.packageName}.generated",
                className = "${setting.contract.className}Impl"
            )
        )

        assertGeneratedFileMatched(result, "/ContractImplementationFactory/ListTypeContract.txt")
    }

    @Test
    fun `testGenerate NoSupertypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.no-supertypes.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.NoSupertypeContractCommand"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationFactoryMainGenerator.generate(
            setting!!,
            properties!!,
            ClassInfo(
                packageName = "${setting.contract.packageName}.generated",
                className = "${setting.contract.className}Impl"
            )
        )

        assertGeneratedFileMatched(result, "/ContractImplementationFactory/NoSupertypeContract.txt")
    }

    @Test
    fun `testGenerate OneSupertypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.one-supertype.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.OneSupertypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationFactoryMainGenerator.generate(
            setting!!,
            properties!!,
            ClassInfo(
                packageName = "${setting.contract.packageName}.generated",
                className = "${setting.contract.className}Impl"
            )
        )

        assertGeneratedFileMatched(result, "/ContractImplementationFactory/OneSupertypeContract.txt")
    }

    @Test
    fun `testGenerate OneSupertypeOverrideContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.one-supertype-override.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.OneSupertypeOverrideContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationFactoryMainGenerator.generate(
            setting!!,
            properties!!,
            ClassInfo(
                packageName = "${setting.contract.packageName}.generated",
                className = "${setting.contract.className}Impl"
            )
        )

        assertGeneratedFileMatched(result, "/ContractImplementationFactory/OneSupertypeOverrideContract.txt")
    }

    // TODO: Add multiple supertypes cases

}