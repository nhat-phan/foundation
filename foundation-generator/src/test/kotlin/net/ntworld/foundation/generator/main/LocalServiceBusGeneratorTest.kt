package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.RequestHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalServiceBusGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate AllInGeneratorTest`() {
        val allSettings = readSettings()
        val file = LocalServiceBusMainGenerator().generate(allSettings.requestHandlers)
        assertGeneratedFileMatched(file, "LocalServiceBus/AllInGeneratorTest.txt")
    }
}