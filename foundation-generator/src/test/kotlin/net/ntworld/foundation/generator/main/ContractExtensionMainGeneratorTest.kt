package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.FileSpec
import net.ntworld.foundation.generator.ContractReader
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractExtensionMainGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate BasicTypeContract`() {
        runTestForContract("BasicTypeContract")
    }

    @Test
    fun `testGenerate ListTypeContract`() {
        runTestForContract("ListTypeContract")
    }

    @Test
    fun `testGenerate NoSupertypeContractCommand`() {
        runTestForContract("NoSupertypeContractCommand")
    }

    @Test
    fun `testGenerate OneSupertypeContract`() {
        runTestForContract("OneSupertypeContract")
    }

    @Test
    fun `testGenerate OneSupertypeOverrideContract`() {
        runTestForContract("OneSupertypeOverrideContract")
    }

    // Bookmark: Add new test case when adding new contract settings
    // TODO: Add multiple supertypes cases

    private fun getFakedFile(): FileSpec.Builder {
        return FileSpec.builder("", "ContractFactories")
    }

    private fun runTestForContract(name: String) {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.$name"
        val setting = allSettings.toMutable().getContract(contract)!!
        val properties = reader.findPropertiesOfContract(contract)!!
        val implementation = ClassInfo(
            packageName = "${setting.contract.packageName}.generated",
            className = "${setting.contract.className}Impl"
        )

        val file = getFakedFile()
        ContractExtensionMainGenerator.generate(setting, properties, implementation, file)
        assertGeneratedContentMatched(file, "ContractExtension/$name.txt")
    }

    private fun assertGeneratedContentMatched(file: FileSpec.Builder, path: String) {
        val stringBuffer = StringBuffer()
        file.build().writeTo(stringBuffer)

        assertEquals(
            readResource("/generated/main/$path"),
            stringBuffer.toString()
        )
    }
}