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
        assertEquals(pinnedContent, file.content)
    }

    val pinnedContent = """package com.example.eventHandler

import com.example.contract.CreatedEvent
import com.example.contract.DeletedEvent
import com.example.contract.UpdatedEvent
import kotlin.Array
import net.ntworld.foundation.Event
import net.ntworld.foundation.EventBus
import net.ntworld.foundation.EventHandler
import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.LocalBusResolver

abstract class LocalEventBus(
  val infrastructure: Infrastructure
) : EventBus, LocalBusResolver<Event, Array<EventHandler<*>>> {
  override fun publish(event: Event) {
    this.process(event)
  }

  override fun process(event: Event) {
    val handlers = this.resolve(event)
    if (null !== handlers) {
      handlers.forEach { it.execute(event = event, message = null) }
    }
  }

  protected abstract fun makeUpdatedEventHandlerOne(): UpdatedEventHandlerOne

  protected abstract fun makeDeletedEventHandler(): DeletedEventHandler

  protected abstract fun make_com_example_eventHandler_anotherDomain_DeletedEventHandler():
      com.example.eventHandler.anotherDomain.DeletedEventHandler

  override fun resolve(instance: Event): Array<EventHandler<*>>? = when (instance) {
    is CreatedEvent -> arrayOf(
      CreatedEventHandler(infrastructure)
    )

    is UpdatedEvent -> arrayOf(
      makeUpdatedEventHandlerOne(),
      UpdatedEventHandlerTwo(infrastructure)
    )

    is DeletedEvent -> arrayOf(
      makeDeletedEventHandler(),
      DeletedEventHandlerTwo(infrastructure),
      make_com_example_eventHandler_anotherDomain_DeletedEventHandler()
    )

    else -> null
  }
}
"""
}