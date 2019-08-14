package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.InfrastructureWrapper
import kotlin.reflect.KClass

open class EventSourcingInfrastructureWrapper(
    private val base: EventSourcingInfrastructure
) : InfrastructureWrapper(base), EventSourcingInfrastructure {
    override fun eventBus(): EventBus {
        return base.eventBus()
    }

    override fun encryptor(): Encryptor {
        return base.encryptor()
    }

    override fun encryptor(cipherId: String, algorithm: String): Encryptor {
        return base.encryptor(cipherId, algorithm)
    }

    override fun eventStreamOf(eventSourced: AbstractEventSourced, version: Int): EventStream {
        return base.eventStreamOf(eventSourced, version)
    }

    override fun eventConverter(eventType: String): EventConverter<Event> {
        return base.eventConverter(eventType)
    }

    override fun <T : Aggregate> snapshotStoreOf(type: KClass<T>): SnapshotStore<T> {
        return base.snapshotStoreOf(type)
    }

}