package net.ntworld.foundation.eventSourcing

data class HydratedEvent(
    private val __event: Event,
    private val __eventData: EventData
) : Event by __event {
    val eventData: EventData = __eventData
}