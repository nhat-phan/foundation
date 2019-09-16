package net.ntworld.foundation

import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.cqrs.ReceivedData
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

open class InfrastructureWrapper(private val base: Infrastructure) : Infrastructure {
    override val root: Infrastructure
        get() = base.root

    override fun wire(root: Infrastructure, list: List<Infrastructure>) {
        this.base.wire(root, list)
    }

    override fun setRoot(root: Infrastructure) {
        this.base.setRoot(root)
    }

    override fun setNext(next: Infrastructure): Infrastructure {
        return this.base.setNext(next)
    }

    override fun environment(): Environment {
        return base.environment()
    }

    override fun <A : Aggregate<D>, D : State> factoryOf(type: KClass<A>): AggregateFactory<A, D> {
        return base.factoryOf(type)
    }

    override fun <T : ReceivedData<Q, R>, Q : Query<R>, R : QueryResult> receiverOf(type: KClass<T>): DataReceiver<T> {
        return base.receiverOf(type)
    }

    override fun <A : Aggregate<D>, D : State> storeOf(type: KClass<A>): StateStore<D> {
        return base.storeOf(type)
    }

    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator {
        return base.idGeneratorOf(type)
    }

    override fun queryBus(): QueryBus {
        return base.queryBus()
    }

    override fun commandBus(): CommandBus {
        return base.commandBus()
    }

    override fun eventBus(): EventBus {
        return base.eventBus()
    }

    override fun serviceBus(): ServiceBus {
        return base.serviceBus()
    }

    override fun encryptor(): Encryptor {
        return base.encryptor()
    }

    override fun encryptor(cipherId: String, algorithm: String): Encryptor {
        return base.encryptor(cipherId, algorithm)
    }

    override fun faker(): Faker {
        return base.faker()
    }

    override fun eventStreamOf(eventSourced: AbstractEventSourced<*>, version: Int): EventStream {
        return base.eventStreamOf(eventSourced, version)
    }

    override fun eventConverterOf(event: Event): EventConverter<Event> {
        return base.eventConverterOf(event)
    }

    override fun eventConverterOf(type: String, variant: Int): EventConverter<Event> {
        return base.eventConverterOf(type, variant)
    }

    override fun <T : Any> messageTranslatorOf(type: KClass<T>): MessageTranslator<T> {
        return base.messageTranslatorOf(type)
    }

    override fun <A : Aggregate<D>, D : State> snapshotStoreOf(type: KClass<A>): SnapshotStore<D> {
        return base.snapshotStoreOf(type)
    }
}