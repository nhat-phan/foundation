package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Event

data class HydratedEvent(
    private val __event: Event,
    private val __eventEntity: EventEntity
) : Event by __event {
    val eventEntity: EventEntity = __eventEntity
}