package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.Event
import net.ntworld.foundation.State

abstract class AbstractEventSourced<T: State> : Aggregate<T> {
    internal var version: Int = 0
        private set

    internal val unpublishedEvents = mutableListOf<Event>()

    private var rehydrating: Boolean = false

    abstract val streamType: String

    abstract fun apply(event: Event)

    internal fun rehydrate(events: Iterable<HydratedEvent>) {
        rehydrating = true
        events.forEach {
            if (version < it.eventEntity.version) {
                apply(it)

                version = it.eventEntity.version
            }
        }
        rehydrating = false
    }

    fun publish(event: Event) {
        if (rehydrating || event is HydratedEvent) {
            return
        }
        unpublishedEvents.add(event)
    }
}