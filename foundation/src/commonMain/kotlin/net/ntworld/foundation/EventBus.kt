package net.ntworld.foundation

import net.ntworld.foundation.eventSourcing.EventEntity

interface EventBus {
    fun publish(event: Event)

    fun process(event: Event) = process(event, null)

    fun process(event: Event, message: Message?)
}
