package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate
import kotlin.reflect.KClass

object Utility {
    fun <T : AbstractEventSourced, A : Aggregate> retrieveEventSourcedAggregate(
        infrastructure: EventSourcingInfrastructure,
        aggregate: A,
        aggregateKlass: KClass<A>,
        eventSourced: T
    ): T {
        val snapshot = infrastructure.snapshotStoreOf(aggregateKlass).findSnapshot(aggregate)
        val stream = infrastructure.eventStreamOf(eventSourced, snapshot.version)

        val converters = mutableMapOf<String, EventConverter<Event>>()
        val events = stream.read().map {
            // TODO: move instance cache mechanism to infrastructure level
            if (!converters.containsKey(it.type)) {
                converters[it.type] = infrastructure.eventConverter(it.type)
            }

            val converter = converters[it.type] as EventConverter<Event>
            HydratedEvent(
                __event = converter.fromEventData(it),
                __eventData = it
            )
        }

        eventSourced.rehydrate(events)
        return eventSourced
    }
}