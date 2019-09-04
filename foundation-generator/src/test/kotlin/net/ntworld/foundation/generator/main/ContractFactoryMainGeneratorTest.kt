package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class ContractFactoryMainGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Empty`() {
        val contractFactoryGenerator = ContractFactoryMainGenerator()
        val result = contractFactoryGenerator.generate("com.example")
        assertGeneratedFileMatched(result, "ContractFactory/Empty.txt")
    }

    @Test
    fun `testGenerate NotEmpty`() {
        val contractFactoryGenerator = ContractFactoryMainGenerator()
        contractFactoryGenerator.add(
            contract = ClassInfo(className = "BasicTypesContract", packageName = "com.example"),
            factory = ClassInfo(className = "BasicTypesContractFactory", packageName = "com.example.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "YourContract", packageName = "com.example"),
            factory = ClassInfo(className = "YourImplementationFactory", packageName = "com.example.generated")
        )

        val result = contractFactoryGenerator.generate("com.example")
        assertGeneratedFileMatched(result, "ContractFactory/NotEmpty.txt")
    }
}