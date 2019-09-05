package net.ntworld.foundation

import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.cqrs.ReceivedData
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

open class InfrastructureContext(open val self: Infrastructure) {
    @InfrastructureDsl.GenericDsl
    fun environment(): Environment = self.environment()

    @InfrastructureDsl.GenericDsl
    fun <A : Aggregate<S>, S : State> factoryOf(type: KClass<A>): AggregateFactory<A, S> = self.factoryOf(type)

    @InfrastructureDsl.GenericDsl
    fun <T : ReceivedData<Q, R>, Q: Query<R>, R: QueryResult> receiverOf(type: KClass<T>): DataReceiver<T> = self.receiverOf(type)

    @InfrastructureDsl.GenericDsl
    fun <A : Aggregate<S>, S : State> storeOf(type: KClass<A>): StateStore<S> = self.storeOf(type)

    @InfrastructureDsl.GenericDsl
    fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator = self.idGeneratorOf(type)

    @InfrastructureDsl.GenericDsl
    fun queryBus(): QueryBus = self.queryBus()

    @InfrastructureDsl.GenericDsl
    fun commandBus(): CommandBus = self.commandBus()

    @InfrastructureDsl.GenericDsl
    fun eventBus(): EventBus = self.eventBus()

    @InfrastructureDsl.GenericDsl
    fun encryptor(): Encryptor = self.encryptor()

    @InfrastructureDsl.GenericDsl
    fun faker(): Faker = self.faker()

    @InfrastructureDsl.GenericDsl
    fun encryptor(cipherId: String, algorithm: String): Encryptor = self.encryptor(cipherId, algorithm)

    @InfrastructureDsl.GenericDsl
    fun eventStreamOf(eventSourced: AbstractEventSourced<*>): EventStream = eventStreamOf(eventSourced, 0)

    @InfrastructureDsl.GenericDsl
    fun eventStreamOf(eventSourced: AbstractEventSourced<*>, version: Int): EventStream =
        self.eventStreamOf(eventSourced, version)

    @InfrastructureDsl.GenericDsl
    fun eventConverter(type: String, variant: Int): EventConverter<Event> = self.eventConverterOf(type, variant)

    @InfrastructureDsl.GenericDsl
    fun <T : Any> messageTranslatorOf(type: KClass<T>): MessageTranslator<T> = self.messageTranslatorOf(type)

    @InfrastructureDsl
    fun <A : Aggregate<S>, S : State> snapshotStoreOf(type: KClass<A>): SnapshotStore<S> = self.snapshotStoreOf(type)

    @InfrastructureDsl.GenericDsl
    inline fun <reified A : Aggregate<S>, S : State> save(instance: A) {
        self.storeOf(A::class).save(instance.state)
    }
}
