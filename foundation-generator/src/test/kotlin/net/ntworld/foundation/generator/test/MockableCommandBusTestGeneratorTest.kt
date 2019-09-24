package net.ntworld.foundation.generator.test

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.CommandHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
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