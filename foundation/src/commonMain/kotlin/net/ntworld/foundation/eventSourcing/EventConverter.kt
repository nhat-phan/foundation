package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Event

/**
 * The converter converts a specific Event to uniform EventEntity for storing to EventStream
 *
 * This interface can be implemented automatically if you use Kotlin Annotation Processor (kapt) "foundation-processor"
 * with @EventSourcing annotation.
 */
interface EventConverter<T : Event> {
    fun toEventEntity(streamId: String, streamType: String, version: Int, event: T): EventEntity

    fun fromEventEntity(eventEntity: EventEntity): T
}