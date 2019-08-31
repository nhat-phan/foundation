package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.EventHandlerSetting
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
            EventHandlerSetting(
                events = listOf(
                    ClassInfo(className = "CreatedEvent", packageName = "com.example.contract")
                ),
                handler = ClassInfo(className = "CreatedEventHandler", packageName = "com.example.eventHandler"),
                makeByFactory = false
            ),
            EventHandlerSetting(
                events = listOf(
                    ClassInfo(className = "UpdatedEvent", packageName = "com.example.contract")
                ),
                handler = ClassInfo(className = "UpdatedEventHandlerOne", packageName = "com.example.eventHandler"),
                makeByFactory = true
            ),
            EventHandlerSetting(
                events = listOf(
                    ClassInfo(className = "UpdatedEvent", packageName = "com.example.contract")
                ),
                handler = ClassInfo(className = "UpdatedEventHandlerTwo", packageName = "com.example.eventHandler"),
                makeByFactory = false
            ),
            EventHandlerSetting(
                events = listOf(
                    ClassInfo(className = "DeletedEvent", packageName = "com.example.contract")
                ),
                handler = ClassInfo(className = "DeletedEventHandler", packageName = "com.example.eventHandler"),
                makeByFactory = true
            ),
            EventHandlerSetting(
                events = listOf(
                    ClassInfo(className = "DeletedEvent", packageName = "com.example.contract")
                ),
                handler = ClassInfo(className = "DeletedEventHandlerTwo", packageName = "com.example.eventHandler"),
                makeByFactory = false
            ),
            EventHandlerSetting(
                events = listOf(
                    ClassInfo(className = "DeletedEvent", packageName = "com.example.contract")
                ),
                handler = ClassInfo(className = "DeletedEventHandler", packageName = "com.example.eventHandler.anotherDomain"),
                makeByFactory = true
            ),
            EventHandlerSetting(
                events = listOf(
                    ClassInfo(className = "CreatedEvent", packageName = "com.example.contract"),
                    ClassInfo(className = "UpdatedEvent", packageName = "com.example.contract"),
                    ClassInfo(className = "DeletedEvent", packageName = "com.example.contract")
                ),
                handler = ClassInfo(className = "AllEventsHandler", packageName = "com.example.eventHandler"),
                makeByFactory = false
            ),
            EventHandlerSetting(
                events = listOf(
                    ClassInfo(className = "CreatedEvent", packageName = "com.example.contract"),
                    ClassInfo(className = "UpdatedEvent", packageName = "com.example.contract"),
                    ClassInfo(className = "DeletedEvent", packageName = "com.example.contract")
                ),
                handler = ClassInfo(className = "AllEventsHandler", packageName = "com.example.eventHandler.anotherDomain"),
                makeByFactory = true
            )
        )

        val file = LocalEventBusGenerator().generate(settings)
        println(file.content)
    }
}