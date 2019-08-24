package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class EventDataGeneratorTest {
    @Test
    fun testGenerate() {
        val settings = EventSettings(
            name = "test.event.CreatedEvent",
            event = ClassInfo(
                packageName = "test.event",
                className = "CreatedEvent"
            ),
            implementation = ClassInfo(
                packageName = "test.event",
                className = "CreatedEventImpl"
            ),
            fields = emptyList(),
            type = "created",
            variant = 0
        )

        val result = EventDataGenerator.generate(settings)
        println(result.content)
    }
}