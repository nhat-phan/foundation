package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventHandlerSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LocalEventBusGeneratorTest {
    @BeforeTest
    fun setUp() {
        GeneratorOutput.setupTest()
    }

    @AfterTest
    fun tearDown() {
        GeneratorOutput.tearDownTest()
    }

    @Test
    fun testGenerate() {
        val settings = listOf(
            EventHandlerSettings(
                event = ClassInfo(className = "CreatedEvent", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "CreatedEventHandler", packageName = "com.example.eventHandler"),
                makeByFactory = false
            ),
            EventHandlerSettings(
                event = ClassInfo(className = "UpdatedEvent", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "UpdatedEventHandlerOne", packageName = "com.example.eventHandler"),
                makeByFactory = true
            ),
            EventHandlerSettings(
                event = ClassInfo(className = "UpdatedEvent", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "UpdatedEventHandlerTwo", packageName = "com.example.eventHandler"),
                makeByFactory = false
            ),
            EventHandlerSettings(
                event = ClassInfo(className = "DeletedEvent", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "DeletedEventHandler", packageName = "com.example.eventHandler"),
                makeByFactory = true
            ),
            EventHandlerSettings(
                event = ClassInfo(className = "DeletedEvent", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "DeletedEventHandlerTwo", packageName = "com.example.eventHandler"),
                makeByFactory = false
            ),
            EventHandlerSettings(
                event = ClassInfo(className = "DeletedEvent", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "DeletedEventHandler", packageName = "com.example.eventHandler.anotherDomain"),
                makeByFactory = true
            )
        )

        val file = LocalEventBusGenerator().generate(settings)
        println(file.content)
    }
}