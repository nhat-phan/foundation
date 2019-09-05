package net.ntworld.foundation

import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.cqrs.ReceivedData
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.eventSourcing.*
import net.ntworld.foundation.exception.CannotResolveException
import kotlin.reflect.KClass

open class InfrastructureResolver(
    next: Infrastructure? = null
) : Infrastructure {


    private var included: List<Infrastructure> = listOf()
    private var next: Infrastructure? = null
    private var nextOrigin: Infrastructure? = next
    private var _root: Infrastructure = this

    init {
        wire()
    }

    override val root: Infrastructure
        get() = _root

    override fun wire(root: Infrastructure, list: List<Infrastructure>) {
        this._root = root
        this.included = list
        wire()
    }

    private fun wire() {
        if (included.isEmpty()) {
            next = nextOrigin
            return
        }

        var accumulator: Infrastructure = this
        for (item in included) {
            item.setRoot(this._root)

            if (accumulator is InfrastructureResolver) {
                accumulator.next = item
            } else {
                accumulator.setNext(item)
            }
            accumulator = item
        }
    }

    override fun setRoot(root: Infrastructure) {
        this._root = root
    }

    override fun setNext(next: Infrastructure): Infrastructure {
        if (included.isEmpty()) {
            this.next = next

            return next
        }
        return included.last().setNext(next)
    }

    override fun environment(): Environment {
        if (null !== next) {
            return next!!.environment()
        }
        throw CannotResolveException("Infrastructure.environment() cannot be resolved")
    }

    override fun <A : Aggregate<D>, D : State> factoryOf(type: KClass<A>): AggregateFactory<A, D> {
        if (null !== next) {
            return next!!.factoryOf(type)
        }
        throw CannotResolveException("Infrastructure.factoryOf() cannot resolve $type")
    }

    override fun <T : ReceivedData<Q, R>, Q: Query<R>, R: QueryResult> receiverOf(type: KClass<T>): DataReceiver<T> {
        if (null !== next) {
            return next!!.receiverOf(type)
        }
        throw CannotResolveException("Infrastructure.receiverOf() cannot resolve $type")
    }

    override fun <A : Aggregate<D>, D : State> storeOf(type: KClass<A>): StateStore<D> {
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

    override fun queryBus(): QueryBus {
        if (null !== next) {
            return next!!.queryBus()
        }
        throw CannotResolveException("Infrastructure.queryBus() cannot be resolved")
    }

    override fun commandBus(): CommandBus {
        if (null !== next) {
            return next!!.commandBus()
        }
        throw CannotResolveException("Infrastructure.commandBus() cannot be resolved")
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

    override fun faker(): Faker {
        if (null !== next) {
            return next!!.faker()
        }
        throw CannotResolveException("Infrastructure.faker() cannot be resolved)")
    }

    override fun eventStreamOf(eventSourced: AbstractEventSourced<*>, version: Int): EventStream {
        if (null !== next) {
            return next!!.eventStreamOf(eventSourced, version)
        }
        throw CannotResolveException("Infrastructure.eventStreamOf() cannot resolve ($eventSourced, $version)")
    }

    override fun eventConverterOf(event: Event): EventConverter<Event> {
        if (null !== next) {
            return next!!.eventConverterOf(event)
        }
        throw CannotResolveException("Infrastructure.eventConverterOf() cannot resolve ($event)")
    }

    override fun eventConverterOf(type: String, variant: Int): EventConverter<Event> {
        if (null !== next) {
            return next!!.eventConverterOf(type, variant)
        }
        throw CannotResolveException("Infrastructure.eventConverterOf() cannot resolve ($type, $variant)")
    }

    override fun <T : Any> messageTranslatorOf(type: KClass<T>): MessageTranslator<T> {
        if (null !== next) {
            return next!!.messageTranslatorOf(type)
        }
        throw CannotResolveException("Infrastructure.messageTranslatorOf() cannot resolve $type")
    }

    override fun <A : Aggregate<D>, D : State> snapshotStoreOf(type: KClass<A>): SnapshotStore<D> {
        if (null !== next) {
            return next!!.snapshotStoreOf(type)
        }
        throw CannotResolveException("Infrastructure.snapshotStoreOf() cannot resolve $type")
    }
}