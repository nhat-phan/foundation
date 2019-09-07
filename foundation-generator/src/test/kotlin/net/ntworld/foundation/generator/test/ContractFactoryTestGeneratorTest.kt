package net.ntworld.foundation.generator.test

import net.ntworld.foundation.generator.Platform
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.test.ContractFactoryTestGenerator
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class ContractFactoryTestGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Jvm-Empty`() {
        val allSettings = readSettings()
        val contractFactoryGenerator = ContractFactoryTestGenerator(Platform.Jvm)
        val result = contractFactoryGenerator.generate(allSettings, "com.generator")
        assertGeneratedFileMatched(result, "ContractFactory/Jvm-Empty.txt")
    }

    @Test
    fun `testGenerate Jvm-NotEmpty`() {
        val allSettings = readSettings()
        val contractFactoryGenerator = ContractFactoryTestGenerator(Platform.Jvm)
        contractFactoryGenerator.add(
            contract = ClassInfo(className = "BasicTypeContract", packageName = "com.generator.contract"),
            implementation = ClassInfo(
                className = "BasicTypeContractImpl",
                packageName = "com.generator.contract.generated"
            )
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "ListTypeContract", packageName = "com.generator.contract"),
            implementation = ClassInfo(className = "ListTypeContractImpl", packageName = "com.generator.contract.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "OneSupertypeContract", packageName = "com.generator.contract"),
            implementation = ClassInfo(className = "OneSupertypeContractImpl", packageName = "com.generator.contract.generated")
        )

        val result = contractFactoryGenerator.generate(allSettings, "com.generator")
        assertGeneratedFileMatched(result, "ContractFactory/Jvm-NotEmpty.txt")
    }
}