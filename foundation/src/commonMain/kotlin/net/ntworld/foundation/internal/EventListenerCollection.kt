package net.ntworld.foundation.internal

import net.ntworld.foundation.eventSourcing.Event
import net.ntworld.foundation.eventSourcing.EventListener

internal class EventListenerCollection {
    private val listeners = mutableListOf<EventListener>()

    fun add(listener: EventListener) {
        listeners.add(listener)
    }

    fun remove(listener: EventListener) {
        listeners.remove(listener)
    }

    fun process(event: Event) {
        listeners.forEach { it.handleEvent(event) }
    }
}
