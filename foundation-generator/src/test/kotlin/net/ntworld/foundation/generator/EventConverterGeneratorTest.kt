package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField
import kotlin.test.Test

class EventConverterGeneratorTest {
    @Test
    fun testGenerate() {
        val settings = EventSettings(
            name = "test.event.CreatedEvent",
            event = ClassInfo(
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
            variant = 1
        )

        val result = EventConverterGenerator.generate(settings)
        println(result.content)
    }
}