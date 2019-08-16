package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.common.ClassInfo
import org.junit.Test

class EventDataMessageConverterGeneratorTest {
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
        EventDataMessageConverterGenerator.buildFile(settings).writeTo(System.out)
    }
}