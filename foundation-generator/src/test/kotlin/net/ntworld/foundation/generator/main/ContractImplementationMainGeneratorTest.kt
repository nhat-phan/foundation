package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.ContractReader
import net.ntworld.foundation.generator.TestSuite
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractImplementationMainGeneratorTest  : TestSuite() {
    // TODO: Add default value case

    @Test
    fun `testGenerate BasicTypeContract`() {
        runTestForContract("BasicTypeContract")
    }

    @Test
    fun `testGenerate ListTypeContract`() {
        runTestForContract("ListTypeContract")
    }

    @Test
    fun `testGenerate NoSupertypeContract`() {
        runTestForContract("NoSupertypeContract")
    }

    @Test
    fun `testGenerate OneSupertypeContract`() {
        runTestForContract("OneSupertypeContract")
    }

    @Test
    fun `testGenerate OneSupertypeOverrideContract`() {
        runTestForContract("OneSupertypeOverrideContract")
    }

    @Test
    fun `testGenerate DefaultValueContract`() {
        val allSettings = readSettingsFromResource("/settings/generator-test.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.generator.contract.DefaultValueContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)
        println(result.content)
        // assertGeneratedFileMatched(result, "/ContractImplementation/OneSupertypeOverrideContract.txt")
    }

    // Bookmark: Add new test case when adding new contract settings
    // TODO: Add multiple supertypes cases

    private fun runTestForContract(name: String) {
        val allSettings = readSettingsFromResource("/settings/generator-test.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.generator.contract.$name"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)
        assertGeneratedFileMatched(result, "/ContractImplementation/$name.txt")
    }
}