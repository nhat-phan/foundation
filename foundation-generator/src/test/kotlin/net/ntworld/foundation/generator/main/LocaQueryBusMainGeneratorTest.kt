package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.QueryHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

class LocaQueryBusMainGeneratorTest: TestSuite() {
    @Test
    fun testGenerate() {
        val allSettings = readSettings()
        val file = LocalQueryBusMainGenerator().generate(allSettings.queryHandlers)
        println(file.content)
    }
}