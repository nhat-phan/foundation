package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.ContractReader
import net.ntworld.foundation.generator.TestSuite
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractImplementationMainGeneratorTest  : TestSuite() {
    // TODO: Add no properties case which should generate normal class

    @Test
    fun `testGenerate BasicTypesContract`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.BasicTypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)

        assertGeneratedFileMatched(result, "ContractImplementation/BasicTypesContract.txt")
    }

    @Test
    fun `testGenerate ListTypeContract`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.ListTypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)

        assertGeneratedFileMatched(result, "ContractImplementation/ListTypeContract.txt")
    }

    @Test
    fun `testGenerate NoSupertypeContract`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.NoSupertypeContractCommand"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)

        assertGeneratedFileMatched(result, "/ContractImplementation/NoSupertypeContract.txt")
    }

    @Test
    fun `testGenerate OneSupertypeContract`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.OneSupertypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)

        assertGeneratedFileMatched(result, "/ContractImplementation/OneSupertypeContract.txt")
    }

    @Test
    fun `testGenerate OneSupertypeOverrideContract`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.OneSupertypeOverrideContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)

        assertGeneratedFileMatched(result, "/ContractImplementation/OneSupertypeOverrideContract.txt")
    }

    @Test
    fun `testGenerate DefaultValueContract`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.DefaultValueContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)
        println(result.content)
        // assertGeneratedFileMatched(result, "/ContractImplementation/OneSupertypeOverrideContract.txt")
    }

    // Bookmark: Add new test case when adding new contract settings
    // TODO: Add multiple supertypes cases
}