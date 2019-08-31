package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class InfrastructureProviderGeneratorTest {
    @Test
    fun testGenerate() {
        val settings = GeneratorSettings(
            provider = "",
            aggregateFactories = listOf(),
            events = listOf(
                EventSourcingSetting(
                    event = ClassInfo(
                        packageName = "com.company.event",
                        className = "CreatedEvent"
                    ),
                    implementation = ClassInfo(
                        packageName = "com.company.event",
                        className = "CreatedEvent"
                    ),
                    fields = emptyList(),
                    type = "created",
                    variant = 0,
                    hasSecondConstructor = false
                ),
                EventSourcingSetting(
                    event = ClassInfo(
                        packageName = "com.company.event",
                        className = "CreatedEventV1"
                    ),
                    implementation = ClassInfo(
                        packageName = "com.company.event",
                        className = "CreatedEventV1Impl"
                    ),
                    fields = emptyList(),
                    type = "created",
                    variant = 1,
                    hasSecondConstructor = false
                ),
                EventSourcingSetting(
                    event = ClassInfo(
                        packageName = "com.company.event",
                        className = "UpdatedEvent"
                    ),
                    implementation = ClassInfo(
                        packageName = "com.company.event",
                        className = "UpdatedEvent"
                    ),
                    fields = emptyList(),
                    type = "updated",
                    variant = 0,
                    hasSecondConstructor = false
                )
            ),
            eventHandlers = listOf(),
            commandHandlers = listOf(),
            queryHandlers = listOf()
        )

        val result = InfrastructureProviderGenerator().generate(settings)
        println(result.content)
    }
}