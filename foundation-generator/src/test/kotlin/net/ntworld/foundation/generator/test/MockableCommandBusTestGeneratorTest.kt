package net.ntworld.foundation.generator.test

import net.ntworld.foundation.generator.TestSuite
import org.junit.Test

class MockableCommandBusTestGeneratorTest: TestSuite() {
    @Test
    fun testGenerate() {
        val settings = readSettings()

        val file = MockableCommandBusTestGenerator().generate(settings)
        // TODO: Add assertion
        // println(file.content)
    }
}