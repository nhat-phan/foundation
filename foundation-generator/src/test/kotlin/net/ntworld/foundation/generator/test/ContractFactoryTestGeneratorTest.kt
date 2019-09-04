package net.ntworld.foundation.generator.test

import net.ntworld.foundation.generator.Platform
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.test.ContractFactoryTestGenerator
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class ContractFactoryTestGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Jvm-Empty`() {
        val contractFactoryGenerator = ContractFactoryTestGenerator(Platform.Jvm)
        val result = contractFactoryGenerator.generate("com.example")
        assertGeneratedFileMatched(result, "ContractFactory/Jvm-Empty.txt")
    }

    @Test
    fun `testGenerate Jvm-NotEmpty`() {
        val contractFactoryGenerator = ContractFactoryTestGenerator(Platform.Jvm)
        contractFactoryGenerator.add(
            contract = ClassInfo(className = "BasicTypesContract", packageName = "com.example"),
            factory = ClassInfo(className = "BasicTypesContractFactory", packageName = "com.example.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "YourContract", packageName = "com.example"),
            factory = ClassInfo(className = "YourImplementationFactory", packageName = "com.example.generated")
        )

        val result = contractFactoryGenerator.generate("com.example")
        assertGeneratedFileMatched(result, "ContractFactory/Jvm-NotEmpty.txt")
    }
}