package net.ntworld.foundation.eventSourcing

interface EventConverter<T : Event> {
    fun toEventData(streamId: String, streamType: String, version: Int, event: T): EventData

    fun fromEventData(data: EventData): T
}