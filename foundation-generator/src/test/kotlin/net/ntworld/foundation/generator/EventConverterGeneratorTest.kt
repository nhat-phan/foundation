package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EventConverterGeneratorTest {
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
        val settings = EventSourcingSetting(
            event = ClassInfo(
                packageName = "test.event",
                className = "CreatedEvent"
            ),
            implementation = ClassInfo(
                packageName = "test.event",
                className = "CreatedEvent"
            ),
            fields = listOf(
                EventField(name = "id", metadata = false, encrypted = false, faked = ""),
                EventField(name = "companyId", metadata = true, encrypted = false, faked = ""),
                EventField(name = "invalidEncrypt", metadata = true, encrypted = true, faked = ""),
                EventField(name = "email", metadata = false, encrypted = true, faked = "email"),
                EventField(name = "firstName", metadata = false, encrypted = true, faked = "firstName"),
                EventField(name = "lastName", metadata = false, encrypted = true, faked = "lastName"),
                EventField(name = "createdAt", metadata = false, encrypted = true, faked = "")
            ),
            type = "any",
            variant = 1,
            hasSecondConstructor = false
        )

        val result = EventConverterGenerator.generate(settings)
        val expected = EventConverterGeneratorTest::class.java
            .getResource("/EventConverter.no-impl.txt").readText()

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
            fields = listOf(
                EventField(name = "id", metadata = false, encrypted = false, faked = ""),
                EventField(name = "companyId", metadata = true, encrypted = false, faked = ""),
                EventField(name = "invalidEncrypt", metadata = true, encrypted = true, faked = ""),
                EventField(name = "email", metadata = false, encrypted = true, faked = "email"),
                EventField(name = "firstName", metadata = false, encrypted = true, faked = "firstName"),
                EventField(name = "lastName", metadata = false, encrypted = true, faked = "lastName"),
                EventField(name = "createdAt", metadata = false, encrypted = true, faked = "")
            ),
            type = "any",
            variant = 1,
            hasSecondConstructor = false
        )

        val result = EventConverterGenerator.generate(settings)
        val expected = EventConverterGeneratorTest::class.java
            .getResource("/EventConverter.impl.txt").readText()

        assertEquals(expected, result.content)
    }
}