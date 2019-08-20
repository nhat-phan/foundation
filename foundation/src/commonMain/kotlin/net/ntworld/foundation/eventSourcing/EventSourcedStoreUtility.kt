package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.StateStore
import net.ntworld.foundation.State
import net.ntworld.foundation.Infrastructure
import kotlin.reflect.KClass

object EventSourcedStoreUtility {
    /**
     * Save event sourced aggregated to store and publish events.
     *
     * There are 4 steps:
     *   1. Convert unpublished events to event data then save to EventStream
     *   2. Publish events via event bus
     *   3. Save snapshot to persistence using SnapshotStore
     *   4. Save given aggregate to persistence using StateStore
     * The step 4 is skipped if SnapshotStore & StateStore are the same instance
     *
     * Please note that because we are using Event Sourcing then the EventStream is
     * single source of truth, then if there is no events this function will do nothing
     */
    fun <A : Aggregate<D>, D : State> savePublishEvents(
        infrastructure: Infrastructure,
        aggregateKlass: KClass<A>,
        store: StateStore<D>,
        eventSourced: AbstractEventSourced<D>
    ): Boolean {
        if (eventSourced.unpublishedEvents.isEmpty()) {
            return true
        }

        val streamId = eventSourced.id
        val streamType = eventSourced.streamType
        val version = eventSourced.version + 1
        val events = eventSourced.unpublishedEvents.mapIndexed { index, event ->
            infrastructure.root.eventConverterOf(event).toEventData(
                streamId,
                streamType,
                version + index,
                event
            )
        }
        infrastructure.root.eventStreamOf(eventSourced).write(events)

        val eventBus = infrastructure.root.eventBus()
        for (i in 0..events.lastIndex) {
            eventBus.publish(events[i], eventSourced.unpublishedEvents[i])
        }

        val snapshotStore = infrastructure.root.snapshotStoreOf(aggregateKlass)
        snapshotStore.saveSnapshot(
            Snapshot(state = eventSourced.state, version = events.last().version)
        )

        if (snapshotStore !== store) {
            store.save(eventSourced.state)
        }
        return true
    }
}