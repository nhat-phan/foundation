package net.ntworld.foundation

import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

open class InfrastructureProvider(
    next: Infrastructure? = null
) : Infrastructure {
    private var included: List<InfrastructureProvider> = listOf()
    private var next: Infrastructure? = null
    private var nextOrigin: Infrastructure? = next
    var root: InfrastructureProvider = this
        private set

    init {
        wire()
    }

    protected fun wire(root: InfrastructureProvider, list: List<InfrastructureProvider>) {
        this.root = root
        this.included = list
        wire()
    }

    private fun wire() {
        if (included.isEmpty()) {
            next = nextOrigin
            return
        }

        var accumulator: InfrastructureProvider = this
        for (index in 0..included.lastIndex) {
            included[index].root = this.root

            accumulator.next = included[index]
            accumulator = included[index]
        }
    }

    override fun setNext(next: Infrastructure): Infrastructure {
        if (included.isEmpty()) {
            this.next = next

            return next
        }
        return included.last().setNext(next)
    }

    override fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A> {
        if (null !== next) {
            return next!!.factoryOf(type)
        }
        throw CannotResolveException("Infrastructure.factoryOf() cannot resolve $type")
    }

    override fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A> {
        if (null !== next) {
            return next!!.storeOf(type)
        }
        throw CannotResolveException("Infrastructure.storeOf() cannot resolve $type")
    }

    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator {
        if (null !== next) {
            return next!!.idGeneratorOf(type)
        }
        throw CannotResolveException("Infrastructure.idGeneratorOf() cannot resolve $type")
    }

    override fun eventBus(): EventBus {
        if (null !== next) {
            return next!!.eventBus()
        }
        throw CannotResolveException("Infrastructure.eventBus() cannot be resolved")
    }

    override fun encryptor(): Encryptor {
        if (null !== next) {
            return next!!.encryptor()
        }
        throw CannotResolveException("Infrastructure.encryptor() cannot be resolved")
    }

    override fun encryptor(cipherId: String, algorithm: String): Encryptor {
        if (null !== next) {
            return next!!.encryptor(cipherId, algorithm)
        }
        throw CannotResolveException("Infrastructure.encryptor() cannot resolve ($cipherId, $algorithm)")
    }

    override fun eventStreamOf(eventSourced: AbstractEventSourced, version: Int): EventStream {
        if (null !== next) {
            return next!!.eventStreamOf(eventSourced, version)
        }
        throw CannotResolveException("Infrastructure.eventStreamOf() cannot resolve ($eventSourced, $version)")
    }

    override fun eventConverter(eventType: String): EventConverter<Event> {
        if (null !== next) {
            return next!!.eventConverter(eventType)
        }
        throw CannotResolveException("Infrastructure.eventConverter() cannot resolve $eventType")
    }

    override fun <T : Aggregate> snapshotStoreOf(type: KClass<T>): SnapshotStore<T> {
        if (null !== next) {
            return next!!.snapshotStoreOf(type)
        }
        throw CannotResolveException("Infrastructure.snapshotStoreOf() cannot resolve $type")
    }
}