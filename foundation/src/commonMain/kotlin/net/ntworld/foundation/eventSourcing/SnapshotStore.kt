package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate

interface SnapshotStore<T: Aggregate> {
    fun saveSnapshot(snapshot: Snapshot<T>)

    fun findSnapshot(aggregate: T): Snapshot<T>
}