package net.ntworld.foundation.generator.test

import net.ntworld.foundation.generator.TestSuite
import org.junit.Test

class MockableServiceBusTestGeneratorTest: TestSuite() {
    @Test
    fun testGenerate() {
        val settings = readSettings()

        val file = MockableServiceBusTestGenerator().generate(settings)
        // TODO: Add assertion
        // println(file.content)
    }
}