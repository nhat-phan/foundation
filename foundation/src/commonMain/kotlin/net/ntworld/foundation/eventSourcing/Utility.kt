package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.Infrastructure
import kotlin.reflect.KClass

object Utility {
    fun <T : AbstractEventSourced, A : Aggregate> retrieveEventSourcedAggregate(
        infrastructure: Infrastructure,
        aggregate: A,
        aggregateKlass: KClass<A>,
        eventSourced: T
    ): T {
        val snapshot = infrastructure.snapshotStoreOf(aggregateKlass).findSnapshot(aggregate)
        val stream = infrastructure.eventStreamOf(eventSourced, snapshot.version)

        val events = stream.read().map {
            HydratedEvent(
                __event = infrastructure.eventConverter(it.type, it.variant).fromEventData(it),
                __eventData = it
            )
        }

        eventSourced.rehydrate(events)
        return eventSourced
    }
}