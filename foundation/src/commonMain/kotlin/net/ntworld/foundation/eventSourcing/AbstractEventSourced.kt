package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate

abstract class AbstractEventSourced : Aggregate {
    internal var version: Int = 0
        private set

    abstract val streamType: String

    abstract fun apply(event: Event)

    internal fun rehydrate(events: Iterable<HydratedEvent>) {
        events.forEach {
            apply(it)

            version = it.eventData.version
        }
    }

    fun publish(event: Event) {
        if (event is HydratedEvent) {
            println("hydrated $event, do not publish")
            return
        }

        println("publishing $event")
    }
}