package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.EventHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalEventBusMainGeneratorTest : TestSuite() {
    @Test
    fun testGenerate() {
        val settings = listOf(
            EventHandlerSetting(
                event = ClassInfo(className = "CreatedEvent", packageName = "com.example.contract"),
                handler = ClassInfo(className = "CreatedEventHandler", packageName = "com.example.eventHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = false
            ),
            EventHandlerSetting(
                event = ClassInfo(className = "UpdatedEvent", packageName = "com.example.contract"),
                handler = ClassInfo(className = "UpdatedEventHandlerOne", packageName = "com.example.eventHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true
            ),
            EventHandlerSetting(
                event = ClassInfo(className = "UpdatedEvent", packageName = "com.example.contract"),
                handler = ClassInfo(className = "UpdatedEventHandlerTwo", packageName = "com.example.eventHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = false
            ),
            EventHandlerSetting(
                event = ClassInfo(className = "DeletedEvent", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeletedEventHandler", packageName = "com.example.eventHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true
            ),
            EventHandlerSetting(
                event = ClassInfo(className = "DeletedEvent", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeletedEventHandlerTwo", packageName = "com.example.eventHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = false
            ),
            EventHandlerSetting(
                event = ClassInfo(className = "DeletedEvent", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeletedEventHandler", packageName = "com.example.eventHandler.anotherDomain"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true
            )
        )

        val file = LocalEventBusMainGenerator().generate(settings)
        // TODO: Rewrite generator and test
        // println(file.content)
    }
}