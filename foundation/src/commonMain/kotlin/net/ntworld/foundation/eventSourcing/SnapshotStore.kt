package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.State

interface SnapshotStore<T : State> {
    fun saveSnapshot(snapshot: Snapshot<T>)

    fun findSnapshotById(id: String): Snapshot<T>?
}