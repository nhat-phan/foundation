package net.ntworld.foundation.generator.test

import net.ntworld.foundation.generator.Platform
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.test.ContractFactoryTestGenerator
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class ContractFactoryTestGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Jvm-Empty`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val contractFactoryGenerator = ContractFactoryTestGenerator(Platform.Jvm)
        val result = contractFactoryGenerator.generate(allSettings, "com.example")
        assertGeneratedFileMatched(result, "ContractFactory/Jvm-Empty.txt")
    }

    @Test
    fun `testGenerate Jvm-NotEmpty`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val contractFactoryGenerator = ContractFactoryTestGenerator(Platform.Jvm)
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
        assertGeneratedFileMatched(result, "ContractFactory/Jvm-NotEmpty.txt")
    }
}