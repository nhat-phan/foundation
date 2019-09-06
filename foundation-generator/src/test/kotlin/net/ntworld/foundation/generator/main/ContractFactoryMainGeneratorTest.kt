package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class ContractFactoryMainGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Empty`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val contractFactoryGenerator = ContractFactoryMainGenerator()
        val result = contractFactoryGenerator.generate(allSettings, "com.example")
        assertGeneratedFileMatched(result, "ContractFactory/Empty.txt")
    }

    @Test
    fun `testGenerate NotEmpty`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val contractFactoryGenerator = ContractFactoryMainGenerator()
        contractFactoryGenerator.add(
            contract = ClassInfo(className = "BasicTypeContract", packageName = "com.example.contract"),
            implementation = ClassInfo(className = "BasicTypeContractImpl", packageName = "com.example.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "ListTypeContract", packageName = "com.example.contract"),
            implementation = ClassInfo(className = "ListTypeContractImpl", packageName = "com.example.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "OneSupertypeContract", packageName = "com.example.contract"),
            implementation = ClassInfo(className = "OneSupertypeContractImpl", packageName = "com.example.generated")
        )

        val result = contractFactoryGenerator.generate(allSettings, "com.example")
        assertGeneratedFileMatched(result, "ContractFactory/NotEmpty.txt")
    }
}