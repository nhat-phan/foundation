package net.ntworld.foundation.eventSourcing

interface EventStream {
    fun isEmpty(): Boolean

    fun write(events: Iterable<EventData>)

    fun read(): Iterable<EventData>
}