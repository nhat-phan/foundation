package net.ntworld.foundation

import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.QueryBus
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

    override fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A> {
        return base.factoryOf(type)
    }

    override fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A> {
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

    override fun encryptor(): Encryptor {
        return base.encryptor()
    }

    override fun encryptor(cipherId: String, algorithm: String): Encryptor {
        return base.encryptor(cipherId, algorithm)
    }

    override fun faker(): Faker {
        return base.faker()
    }

    override fun eventStreamOf(eventSourced: AbstractEventSourced, version: Int): EventStream {
        return base.eventStreamOf(eventSourced, version)
    }

    override fun eventConverterOf(event: Event): EventConverter<Event> {
        return base.eventConverterOf(event)
    }

    override fun eventConverterOf(type: String, variant: Int): EventConverter<Event> {
        return base.eventConverterOf(type, variant)
    }

    override fun <T : Any> messageConverterOf(type: KClass<T>): MessageConverter<T> {
        return base.messageConverterOf(type)
    }

    override fun <T : Aggregate> snapshotStoreOf(type: KClass<T>): SnapshotStore<T> {
        return base.snapshotStoreOf(type)
    }
}