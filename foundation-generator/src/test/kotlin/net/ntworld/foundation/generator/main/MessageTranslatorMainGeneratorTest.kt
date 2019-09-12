package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.GeneratorTest
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.util.ContractReader
import kotlin.test.Test

class MessageTranslatorMainGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate BasicTypeContract`() {
        runTestForContract("BasicTypeContract")
    }

    // Bookmark: Add new test case when adding new contract settings
    // TODO: Add multiple supertypes cases

    private fun runTestForContract(name: String) {
        val allSettings = readSettingsFromResource("/settings/generator-test.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations, allSettings.fakedProperties)
        val contract = GeneratorTest.Contract.namespace(name)
        val setting = allSettings.toMutable().getContract(contract)

        val result = MessageTranslatorMainGenerator.generate(
            setting!!,
            ClassInfo("${setting.contract.className}Impl", setting.contract.packageName),
            reader.findPropertiesOfContract(GeneratorTest.Contract.namespace(name))!!
        )
        println(result.content)
        // assertGeneratedFileMatched(result, "/ContractImplementation/$name.txt")
    }
}