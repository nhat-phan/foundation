package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class ContractFactoryMainGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Empty`() {
        val allSettings = readSettings()
        val contractFactoryGenerator = ContractFactoryMainGenerator()
        val result = contractFactoryGenerator.generate(allSettings, "com.generator")
        assertGeneratedFileMatched(result, "ContractFactory/Empty.txt")
    }

    @Test
    fun `testGenerate NotEmpty`() {
        val allSettings = readSettings()
        val contractFactoryGenerator = ContractFactoryMainGenerator()
        contractFactoryGenerator.add(
            contract = ClassInfo(className = "BasicTypeContract", packageName = "com.generator.contract"),
            implementation = ClassInfo(className = "BasicTypeContractImpl", packageName = "com.generator.contract.generated")
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
        assertGeneratedFileMatched(result, "ContractFactory/NotEmpty.txt")
    }
}