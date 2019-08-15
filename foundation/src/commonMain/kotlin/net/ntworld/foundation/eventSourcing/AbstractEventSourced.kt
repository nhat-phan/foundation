package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate

abstract class AbstractEventSourced : Aggregate {
    internal var version: Int = 0
        private set

    internal val unpublishedEvents = mutableListOf<Event>()

    abstract val streamType: String

    abstract fun apply(event: Event)

    internal fun rehydrate(events: Iterable<HydratedEvent>) {
        events.forEach {
            if (version < it.eventData.version) {
                apply(it)

                version = it.eventData.version
            }
        }
    }

    fun publish(event: Event) {
        if (event is HydratedEvent) {
            println("hydrated $event, do not publish")
            return
        }

        println("put $event for publishing later")
        unpublishedEvents.add(event)
    }
}