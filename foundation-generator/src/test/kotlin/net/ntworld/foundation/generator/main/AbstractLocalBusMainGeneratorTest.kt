package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.GeneratorTest
import net.ntworld.foundation.generator.TestSuite
import kotlin.test.Test

class AbstractLocalBusMainGeneratorTest: TestSuite() {
    @Test
    fun `testGenerate WithDifferentConstructors`() {
        val allSettings = readSettings().toMutable()
        val settings = listOf(
            allSettings.getRequestHandler(GeneratorTest.RequestHandler.CreateUserRequestHandler)!!,
            allSettings.getRequestHandler(GeneratorTest.RequestHandler.GetUserByIdRequestHandler)!!
        )
        val file = LocalServiceBusMainGenerator().generate(settings)
        println(file.content)
    }
}