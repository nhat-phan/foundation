package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventDataSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class EventDataGeneratorTest {
    @Test
    fun testGenerate() {
        val settings = EventDataSettings(
            event = ClassInfo(
                packageName = "test.event",
                className = "CreatedEvent"
            ),
            type = "created",
            variant = 0
        )
        EventDataGenerator.buildFile(settings).writeTo(System.out)
    }
}