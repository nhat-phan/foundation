package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class EventDataGeneratorTest {
    @Test
    fun testGenerate() {
        val settings = EventSettings(
            event = ClassInfo(
                packageName = "test.event",
                className = "CreatedEvent"
            ),
            fields = emptyList(),
            type = "created",
            variant = 0
        )

        val result = EventDataGenerator.generate(settings)
        println(result.content)
    }
}