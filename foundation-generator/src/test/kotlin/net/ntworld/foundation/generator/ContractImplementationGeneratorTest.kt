package net.ntworld.foundation.generator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractImplementationGeneratorTest {
    @BeforeTest
    fun setUp() {
        GeneratorOutput.setupTest()
    }

    @AfterTest
    fun tearDown() {
        GeneratorOutput.tearDownTest()
    }

    @Test
    fun `testGenerate BasicTypesContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.basic-types.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.BasicTypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationGenerator.generate(setting!!, properties!!)

        assertEquals(readResource("/ContractImplementation.basic-types.txt"), result.content)
    }

    @Test
    fun `testGenerate ListTypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.list-type.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.ListTypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationGenerator.generate(setting!!, properties!!)

        assertEquals(readResource("/ContractImplementation.list-type.txt"), result.content)
    }

    @Test
    fun `testGenerate NoSupertypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.no-supertypes.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.NoSupertypeContractCommand"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationGenerator.generate(setting!!, properties!!)

        assertEquals(readResource("/ContractImplementation.no-supertypes.txt"), result.content)
    }

    @Test
    fun `testGenerate OneSupertypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.one-supertype.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.OneSupertypeContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationGenerator.generate(setting!!, properties!!)

        assertEquals(readResource("/ContractImplementation.one-supertype.txt"), result.content)
    }

    @Test
    fun `testGenerate OneSupertypeOverrideContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.one-supertype-override.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        val contract = "com.example.contract.OneSupertypeOverrideContract"
        val setting = allSettings.toMutable().getContract(contract)
        val properties = reader.findPropertiesOfContract(contract)

        val result = ContractImplementationGenerator.generate(setting!!, properties!!)

        assertEquals(readResource("/ContractImplementation.one-supertype-override.txt"), result.content)
    }

    // TODO: Add multiple supertypes cases

    private fun readSettingsFromResource(path: String): GeneratorSettings {
        val json = Json(JsonConfiguration.Stable)
        return json.parse(GeneratorSettings.serializer(), readResource(path))
    }

    private fun readResource(path: String): String {
        return ContractImplementationGeneratorTest::class.java.getResource(path).readText()
    }
}