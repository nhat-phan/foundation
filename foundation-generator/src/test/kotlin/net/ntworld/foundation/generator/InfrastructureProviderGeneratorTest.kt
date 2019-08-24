package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class InfrastructureProviderGeneratorTest {
    @Test
    fun testGenerate() {
        val settings = GeneratorSettings(
            aggregateFactories = listOf(),
            events = listOf(
                EventSettings(
                    name = "test.event.CreatedEvent",
                    event = ClassInfo(
                        packageName = "com.company.event",
                        className = "CreatedEvent"
                    ),
                    fields = emptyList(),
                    type = "created",
                    variant = 0
                ),
                EventSettings(
                    name = "test.event.UpdatedEvent",
                    event = ClassInfo(
                        packageName = "com.company.event",
                        className = "UpdatedEvent"
                    ),
                    fields = emptyList(),
                    type = "updated",
                    variant = 0
                )
            )
        )

        val result = InfrastructureProviderGenerator().generate(settings)
        println(result.content)
    }
}