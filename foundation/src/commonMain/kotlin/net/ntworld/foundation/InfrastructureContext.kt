package net.ntworld.foundation

import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

open class InfrastructureContext(open val self: Infrastructure) {
    @InfrastructureDsl
    fun environment(): Environment = self.environment()

    @InfrastructureDsl
    fun <A : Aggregate<S>, S : State> factoryOf(type: KClass<A>): AggregateFactory<A, S> = self.factoryOf(type)

    @InfrastructureDsl
    fun <A : Aggregate<S>, S : State> storeOf(type: KClass<A>): StateStore<S> = self.storeOf(type)

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
    fun faker(): Faker = self.faker()

    @InfrastructureDsl
    fun encryptor(cipherId: String, algorithm: String): Encryptor = self.encryptor(cipherId, algorithm)

    @InfrastructureDsl
    fun eventStreamOf(eventSourced: AbstractEventSourced<*>): EventStream = eventStreamOf(eventSourced, 0)

    @InfrastructureDsl
    fun eventStreamOf(eventSourced: AbstractEventSourced<*>, version: Int): EventStream =
        self.eventStreamOf(eventSourced, version)

    @InfrastructureDsl
    fun eventConverter(type: String, variant: Int): EventConverter<Event> = self.eventConverterOf(type, variant)

    @InfrastructureDsl
    fun <T : Any> messageTranslatorOf(type: KClass<T>): MessageTranslator<T> = self.messageTranslatorOf(type)

    @InfrastructureDsl
    fun <A : Aggregate<S>, S : State> snapshotStoreOf(type: KClass<A>): SnapshotStore<S> = self.snapshotStoreOf(type)

    @InfrastructureDsl
    inline fun <reified A : Aggregate<S>, S : State> save(instance: A) {
        self.storeOf(A::class).save(instance.state)
    }
}
