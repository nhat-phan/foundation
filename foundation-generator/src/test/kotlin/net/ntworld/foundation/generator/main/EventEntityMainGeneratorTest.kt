package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class EventEntityMainGeneratorTest : TestSuite() {
    @Test
    fun `test generate when the implementation and event are the same`() {
        val settings = EventSourcingSetting(
            event = ClassInfo(
                packageName = "test.event",
                className = "CreatedEvent"
            ),
            implementation = ClassInfo(
                packageName = "test.event",
                className = "CreatedEvent"
            ),
            fields = emptyList(),
            type = "created",
            variant = 0,
            hasSecondConstructor = false
        )

        val result = EventEntityMainGenerator.generate(settings)
        val expected = readResource("/EventEntity.no-impl.txt")

        assertEquals(expected, result.content)
    }

    @Test
    fun `test generate when the implementation and event are NOT the same`() {
        val settings = EventSourcingSetting(
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
            variant = 0,
            hasSecondConstructor = false
        )

        val result = EventEntityMainGenerator.generate(settings)
        val expected = readResource("/EventEntity.impl.txt")

        assertEquals(expected, result.content)
    }
}