package net.ntworld.foundation.eventSourcing

interface EventStream {
    fun isEmpty(): Boolean

    fun write(events: Iterable<EventEntity>)

    fun read(): Iterable<EventEntity>
}