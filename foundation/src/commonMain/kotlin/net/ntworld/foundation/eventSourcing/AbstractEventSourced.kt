package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate

abstract class AbstractEventSourced : Aggregate {
    internal var version: Int = 0
        private set

    internal val unpublishedEvents = mutableListOf<Event>()

    private var rehydrating: Boolean = false

    abstract val streamType: String

    abstract fun apply(event: Event)

    internal fun rehydrate(events: Iterable<HydratedEvent>) {
        rehydrating = true
        events.forEach {
            if (version < it.eventData.version) {
                apply(it)

                version = it.eventData.version
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