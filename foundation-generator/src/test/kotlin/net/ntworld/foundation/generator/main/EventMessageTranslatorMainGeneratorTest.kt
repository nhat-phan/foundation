package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField
import kotlin.test.Test
import kotlin.test.assertEquals

class EventMessageTranslatorMainGeneratorTest: TestSuite() {
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

        val result = EventMessageTranslatorMainGenerator.generate(settings)
        val expected = readResource("/EventMessageTranslator.no-impl.txt")

        assertEquals(expected, result.content)
    }

    @Test
    fun `test generate when the implementation and event are NOT the same, no 2nd constructor`() {
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

        val result = EventMessageTranslatorMainGenerator.generate(settings)
        val expected = readResource("/EventMessageTranslator.impl.no-2nd-ctor.txt")

        assertEquals(expected, result.content)
    }

    @Test
    fun `test generate when the implementation and event are NOT the same, with 2nd constructor`() {
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
            hasSecondConstructor = true
        )

        val result = EventMessageTranslatorMainGenerator.generate(settings)
        val expected = readResource("/EventMessageTranslator.impl.2nd-ctor.txt")

        assertEquals(expected, result.content)
    }
}