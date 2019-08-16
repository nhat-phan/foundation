package net.ntworld.foundation

import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

open class InfrastructureContext(open val self: Infrastructure) {
    @InfrastructureDsl
    fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A> = self.factoryOf(type)

    @InfrastructureDsl
    fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A> = self.storeOf(type)

    @InfrastructureDsl
    fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator = self.idGeneratorOf(type)

    @InfrastructureDsl
    fun queryBus(): QueryBus = self.queryBus()

    @InfrastructureDsl
    fun commandBus(): CommandBus = self.commandBus()

    @InfrastructureDsl
    fun eventBus(): EventBus = self.eventBus()

    @InfrastructureDsl
    fun encryptor(): Encryptor = self.encryptor()

    @InfrastructureDsl
    fun encryptor(cipherId: String, algorithm: String): Encryptor = self.encryptor(cipherId, algorithm)

    @InfrastructureDsl
    fun eventStreamOf(eventSourced: AbstractEventSourced): EventStream = eventStreamOf(eventSourced, 0)

    @InfrastructureDsl
    fun eventStreamOf(eventSourced: AbstractEventSourced, version: Int): EventStream =
        self.eventStreamOf(eventSourced, version)

    @InfrastructureDsl
    fun eventConverter(type: String, variant: Int): EventConverter<Event> = self.eventConverterOf(type, variant)

    @InfrastructureDsl
    fun <T : Any> messageConverterOf(type: KClass<T>): MessageConverter<T> = self.messageConverterOf(type)

    @InfrastructureDsl
    fun <T : Aggregate> snapshotStoreOf(type: KClass<T>): SnapshotStore<T> = self.snapshotStoreOf(type)

    @InfrastructureDsl
    inline fun <reified T : Aggregate> save(instance: T) {
        self.storeOf(T::class).save(instance)
    }
}
