package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsSerializerTest {
    @Test
    fun testSerializeEventDataSettings() {
        val settings = EventSettings(
            name = "test.event.AggregateCreatedEvent",
            event = ClassInfo(
                packageName = "test.event",
                className = "AggregateCreatedEvent"
            ),
            implementation = ClassInfo(
                packageName = "test.event",
                className = "AggregateCreatedEvent"
            ),
            fields = emptyList(),
            type = "com.test.aggregate:created",
            variant = 0
        )
        val output = """{
    "name": "test.event.AggregateCreatedEvent",
    "event": {
        "className": "AggregateCreatedEvent",
        "packageName": "test.event"
    },
    "implementation": {
        "className": "AggregateCreatedEvent",
        "packageName": "test.event"
    },
    "fields": [
    ],
    "type": "com.test.aggregate:created",
    "variant": 0
}"""
        assertEquals(output, SettingsSerializer.serialize(settings))

        val parsed = SettingsSerializer.parseEventSettings(output)
        assertTrue(parsed.equals(settings))
    }
}