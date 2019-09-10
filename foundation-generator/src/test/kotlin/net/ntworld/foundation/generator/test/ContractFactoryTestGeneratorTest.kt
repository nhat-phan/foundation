package net.ntworld.foundation.generator.test

import net.ntworld.foundation.generator.GeneratorTest
import net.ntworld.foundation.generator.Platform
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.test.ContractFactoryTestGenerator
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractFactoryTestGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Jvm-Empty`() {
        val allSettings = readSettings()
        val contractFactoryGenerator = ContractFactoryTestGenerator(Platform.Jvm)
        val result = contractFactoryGenerator.generate(
            allSettings,
            ClassInfo(className = "TestUtility", packageName = "com.generator"),
            GeneratorTest.namespace()
        )
        assertGeneratedFileMatched(result, "ContractFactory/Jvm-Empty.txt")
    }

    @Test
    fun `testGenerate Jvm-NotEmpty`() {
        val allSettings = readSettings()
        val contractFactoryGenerator = ContractFactoryTestGenerator(Platform.Jvm)
        val basePackageName = GeneratorTest.Contract.namespace()
        contractFactoryGenerator.add(
            contract = ClassInfo(className = "BasicTypeContract", packageName = basePackageName),
            implementation = ClassInfo(
                className = "BasicTypeContractImpl",
                packageName = "com.generator.contract.generated"
            )
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "ListTypeContract", packageName = basePackageName),
            implementation = ClassInfo(className = "ListTypeContractImpl", packageName = "$basePackageName.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "OneSupertypeContract", packageName = basePackageName),
            implementation = ClassInfo(
                className = "OneSupertypeContractImpl",
                packageName = "$basePackageName.generated"
            )
        )

        val result = contractFactoryGenerator.generate(
            allSettings,
            ClassInfo(className = "TestUtility", packageName = "com.generator"),
            GeneratorTest.namespace()
        )
        assertEquals("ContractFactoryTest", result.target.className)
        assertGeneratedFileMatched(result, "ContractFactory/Jvm-NotEmpty.txt")
    }
}