package net.ntworld.foundation.generator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractFactoryGeneratorTest {
    @BeforeTest
    fun setUp() {
        GeneratorOutput.setupTest()
    }

    @AfterTest
    fun tearDown() {
        GeneratorOutput.tearDownTest()
    }

    @Test
    fun testGenerate() {
        val contractFactoryGenerator = ContractFactoryGenerator()
        contractFactoryGenerator.add(
            contract = ClassInfo(className = "BasicTypesContract", packageName = "com.example"),
            factory = ClassInfo(className = "BasicTypesContractFactory", packageName = "com.example.generated")
        )

        contractFactoryGenerator.add(
            contract = ClassInfo(className = "YourContract", packageName = "com.example"),
            factory = ClassInfo(className = "YourImplementationFactory", packageName = "com.example.generated")
        )

        val result = contractFactoryGenerator.generate("com.example")
        println(result.content)
    }

    private fun readSettingsFromResource(path: String): GeneratorSettings {
        val json = Json(JsonConfiguration.Stable)
        return json.parse(GeneratorSettings.serializer(), readResource(path))
    }

    private fun readResource(path: String): String {
        return ContractImplementationGeneratorTest::class.java.getResource(path).readText()
    }
}