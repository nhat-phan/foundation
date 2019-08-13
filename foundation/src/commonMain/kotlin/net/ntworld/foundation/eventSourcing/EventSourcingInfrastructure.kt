package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.Infrastructure
import kotlin.reflect.KClass

interface EventSourcingInfrastructure : Infrastructure {
    fun eventBus(): EventBus

    fun encryptor(): Encryptor

    fun encryptor(cipherId: String, algorithm: String): Encryptor

    fun eventStreamOf(eventSourced: AbstractEventSourced): EventStream = eventStreamOf(eventSourced, 0)

    fun eventStreamOf(eventSourced: AbstractEventSourced, version: Int): EventStream

    fun eventConverter(eventType: String): EventConverter<Event>

    fun <T : Aggregate> snapshotStoreOf(type: KClass<T>): SnapshotStore<T>
}
