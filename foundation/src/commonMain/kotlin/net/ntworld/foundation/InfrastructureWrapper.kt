package net.ntworld.foundation

import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

open class InfrastructureWrapper(private val base: Infrastructure) : Infrastructure {
    override fun setNext(next: Infrastructure): Infrastructure {
        if (this.base is InfrastructureProvider) {
            return this.base.setNext(next)
        }
        throw CannotSetInfrastructureProviderChain()
    }

    override fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A> {
        return base.factoryOf(type)
    }

    override fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A> {
        return base.storeOf(type)
    }

    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator {
        return base.idGeneratorOf(type)
    }

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

    override fun eventConverter(type: String, variant: Int): EventConverter<Event> {
        return base.eventConverter(type, variant)
    }

    override fun <T : Aggregate> snapshotStoreOf(type: KClass<T>): SnapshotStore<T> {
        return base.snapshotStoreOf(type)
    }
}