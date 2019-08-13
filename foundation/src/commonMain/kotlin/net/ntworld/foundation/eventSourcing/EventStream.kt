package net.ntworld.foundation.eventSourcing

interface EventStream {
    fun write(events: Iterable<EventData>)

    fun read(): Iterable<EventData>
}