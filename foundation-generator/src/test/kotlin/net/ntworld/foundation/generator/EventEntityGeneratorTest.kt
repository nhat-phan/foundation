package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EventEntityGeneratorTest {
    @BeforeTest
    fun setUp() {
        GeneratorOutput.setupTest()
    }

    @AfterTest
    fun tearDown() {
        GeneratorOutput.tearDownTest()
    }

    @Test
    fun `test generate when the implementation and event are the same`() {
        val settings = EventSettings(
            name = "test.event.CreatedEvent",
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

        val result = EventEntityGenerator.generate(settings)
        val expected = EventEntityGeneratorTest::class.java
            .getResource("/EventEntity.no-impl.txt").readText()

        assertEquals(expected, result.content)
    }

    @Test
    fun `test generate when the implementation and event are NOT the same`() {
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
            variant = 0,
            hasSecondConstructor = false
        )

        val result = EventEntityGenerator.generate(settings)
        val expected = EventEntityGeneratorTest::class.java
            .getResource("/EventEntity.impl.txt").readText()

        assertEquals(expected, result.content)
    }
}