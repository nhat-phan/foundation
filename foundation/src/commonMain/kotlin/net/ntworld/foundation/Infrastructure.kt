package net.ntworld.foundation

import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

interface Infrastructure {
    fun setNext(next: Infrastructure): Infrastructure

    fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A>

    fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A>

    fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator

    fun idGeneratorOf(): IdGenerator = idGeneratorOf(Any::class)

    fun eventBus(): EventBus

    fun encryptor(): Encryptor

    fun encryptor(cipherId: String, algorithm: String): Encryptor

    fun eventStreamOf(eventSourced: AbstractEventSourced): EventStream = eventStreamOf(eventSourced, 0)

    fun eventStreamOf(eventSourced: AbstractEventSourced, version: Int): EventStream

    fun eventConverter(eventType: String): EventConverter<Event>

    fun <T : Aggregate> snapshotStoreOf(type: KClass<T>): SnapshotStore<T>

    operator fun <T> invoke(block: InfrastructureContext.() -> T): T {
        return block.invoke(InfrastructureContext(this))
    }
}
