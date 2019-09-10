package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.FileSpec
import net.ntworld.foundation.generator.util.ContractReader
import net.ntworld.foundation.generator.GeneratorTest
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractExtensionTestGeneratorTest: TestSuite() {
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

    private fun getFakedFile(): FileSpec.Builder {
        return FileSpec.builder("", "ContractFactories")
    }

    private fun runTestForContract(name: String) {
        val allSettings = readSettings()
        val reader =
            ContractReader(allSettings.contracts, allSettings.fakedAnnotations, allSettings.fakedProperties)
        val contract = GeneratorTest.Contract.namespace(name)
        val setting = allSettings.toMutable().getContract(contract)!!
        val properties = reader.findPropertiesOfContract(contract)!!
        val implementation = ClassInfo(
            packageName = "${setting.contract.packageName}.generated",
            className = "${setting.contract.className}Impl"
        )

        val file = getFakedFile()
        ContractExtensionTestGenerator.generate(setting, properties, implementation, file)
        assertGeneratedContentMatched(file, "ContractExtension/$name.txt")
    }

    private fun assertGeneratedContentMatched(file: FileSpec.Builder, path: String) {
        val stringBuffer = StringBuffer()
        file.build().writeTo(stringBuffer)
        assertEquals(
            readResource("/generated/test/$path"),
            stringBuffer.toString()
        )
    }
}