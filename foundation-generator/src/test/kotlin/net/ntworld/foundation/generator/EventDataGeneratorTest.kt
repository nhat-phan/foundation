package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.common.ClassInfo
import kotlin.test.Test

class EventDataGeneratorTest {
    @Test
    fun testGenerate() {
        val settings = EventDataGeneratorSettings(
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