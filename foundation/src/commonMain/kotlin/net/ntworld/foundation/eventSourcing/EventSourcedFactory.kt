package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.State
import net.ntworld.foundation.Infrastructure
import kotlin.reflect.KClass

object EventSourcedFactory {
    fun <T : AbstractEventSourced<*>, A : Aggregate<S>, S: State> retrieve(
        infrastructure: Infrastructure,
        aggregateKlass: KClass<A>,
        aggregateId: String,
        eventSourcedMaker: (S?) -> T
    ): T? {
        val snapshot = infrastructure.root.snapshotStoreOf(aggregateKlass).findSnapshotById(aggregateId)
        if (null !== snapshot) {
            return rehydrate(
                infrastructure = infrastructure,
                eventSourced = eventSourcedMaker.invoke(snapshot.state),
                version = snapshot.version
            )
        }

        val eventSourced = eventSourcedMaker.invoke(null)
        return rehydrate(
            infrastructure = infrastructure,
            eventSourced = eventSourced,
            version = 0
        )
    }

    internal fun <T : AbstractEventSourced<*>> rehydrate(
        infrastructure: Infrastructure,
        eventSourced: T,
        version: Int
    ): T? {
        val stream = infrastructure.root.eventStreamOf(eventSourced, version)
        if (version == 0 && stream.isEmpty()) {
            return null
        }

        val events = stream.read().map {
            HydratedEvent(
                __event = infrastructure.root.eventConverterOf(it.type, it.variant).fromEventData(it),
                __eventData = it
            )
        }

        eventSourced.rehydrate(events)
        return eventSourced
    }
}