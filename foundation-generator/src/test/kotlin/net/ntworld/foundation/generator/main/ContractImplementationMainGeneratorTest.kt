package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.ContractReader
import net.ntworld.foundation.generator.GeneratorTest
import net.ntworld.foundation.generator.TestSuite
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractImplementationMainGeneratorTest  : TestSuite() {
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
        runTestForContract("DefaultValueContract")
    }

    @Test
    fun `testGenerate CustomTypeContract`() {
        runTestForContract("CustomTypeContract")
        runTestForContract("CustomTypeContractAddress")
    }

    // Bookmark: Add new test case when adding new contract settings
    // TODO: Add multiple supertypes cases

    private fun runTestForContract(name: String) {
        val allSettings = readSettingsFromResource("/settings/generator-test.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = GeneratorTest.Contract.namespace(name)
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationMainGenerator.generate(setting!!, properties!!)
        assertGeneratedFileMatched(result, "/ContractImplementation/$name.txt")
    }
}