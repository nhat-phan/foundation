package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.AggregateStore
import net.ntworld.foundation.Infrastructure
import kotlin.reflect.KClass

object Utility {
    /**
     * Save event sourced aggregated to store and publish events.
     *
     * There are 4 steps:
     *   1. Save give aggregate to persistence using AggregateStore
     *   2. Convert unpublished events to event data then save to EventStream
     *   3. Publish events via event bus
     *   4. Save snapshot to persistence using SnapshotStore
     * The step 2/3/4 never run if there is no unpublished events in eventSourced
     */
    fun <A : Aggregate> saveEventSourcedAggregateAndPublishEvents(
        infrastructure: Infrastructure,
        aggregateKlass: KClass<A>,
        store: AggregateStore<A>,
        data: A,
        eventSourced: AbstractEventSourced
    ): Boolean {
        if (eventSourced.unpublishedEvents.isEmpty()) {
            return store.save(data)
        }

        if (!store.save(data)) {
            return false
        }

        val streamId = eventSourced.id
        val streamType = eventSourced.streamType
        val version = eventSourced.version + 1
        val events = eventSourced.unpublishedEvents.mapIndexed { index, event ->
            infrastructure.eventConverterOf(event).toEventData(
                streamId,
                streamType,
                version + index,
                event
            )
        }
        infrastructure.eventStreamOf(eventSourced).write(events)

        val eventBus = infrastructure.eventBus()
        for (event in eventSourced.unpublishedEvents) {
            eventBus.publish(event)
        }

        infrastructure.snapshotStoreOf(aggregateKlass).saveSnapshot(
            Snapshot(aggregate = eventSourced, version = events.last().version) as Snapshot<A>
        )
        return true
    }

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
                __event = infrastructure.eventConverterOf(it.type, it.variant).fromEventData(it),
                __eventData = it
            )
        }

        eventSourced.rehydrate(events)
        return eventSourced
    }
}