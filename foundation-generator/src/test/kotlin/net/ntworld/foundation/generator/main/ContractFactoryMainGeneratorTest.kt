package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.GeneratorTest
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class ContractFactoryMainGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Empty`() {
        val allSettings = readSettings()
        val contractFactoryGenerator = ContractFactoryMainGenerator()
        val result = contractFactoryGenerator.generate(allSettings, GeneratorTest.namespace())
        assertGeneratedFileMatched(result, "ContractFactory/Empty.txt")
    }

    @Test
    fun `testGenerate NotEmpty`() {
        val allSettings = readSettings()
        val contractFactoryGenerator = ContractFactoryMainGenerator()
        val basePackageName = GeneratorTest.Contract.namespace()
        contractFactoryGenerator.add(
            contract = ClassInfo(className = "BasicTypeContract", packageName = basePackageName),
            implementation = ClassInfo(className = "BasicTypeContractImpl", packageName = "$basePackageName.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "ListTypeContract", packageName = basePackageName),
            implementation = ClassInfo(className = "ListTypeContractImpl", packageName = "$basePackageName.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "OneSupertypeContract", packageName = basePackageName),
            implementation = ClassInfo(className = "OneSupertypeContractImpl", packageName = "$basePackageName.generated")
        )

        val result = contractFactoryGenerator.generate(allSettings, GeneratorTest.namespace())
        assertGeneratedFileMatched(result, "ContractFactory/NotEmpty.txt")
    }
}