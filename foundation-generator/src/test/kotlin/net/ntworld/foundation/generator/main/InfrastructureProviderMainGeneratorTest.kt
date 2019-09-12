package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class InfrastructureProviderMainGeneratorTest : TestSuite() {
    @Test
    fun testGenerate() {
        val settings = GeneratorSettings(
            globalDirectory = "",
            aggregateFactories = listOf(),
            annotationProcessorRunInfo = listOf(),
            eventSourcings = listOf(
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
            queryHandlers = listOf(),
            requestHandlers = listOf(),
            implementations = listOf(),
            messagings = listOf(),
            contracts = listOf(),
            fakedAnnotations = listOf(),
            fakedProperties = listOf()
        )

        val result = InfrastructureProviderMainGenerator().generate(settings)

        // TODO: Rewrite generator and test
        // println(result.content)
    }
}