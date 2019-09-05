package net.ntworld.foundation

import net.ntworld.foundation.cqrs.*
import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

interface Infrastructure {
    val root: Infrastructure

    fun wire(root: Infrastructure, list: List<Infrastructure>)

    fun setRoot(root: Infrastructure)

    fun setNext(next: Infrastructure): Infrastructure

    fun environment(): Environment

    // fun <T : Any> resolve(type: KClass<T>): T

    fun <A : Aggregate<S>, S : State> factoryOf(type: KClass<A>): AggregateFactory<A, S>

    fun <T : ReceivedData<Q, R>, Q : Query<R>, R: QueryResult> receiverOf(type: KClass<T>): DataReceiver<T>

    fun <A : Aggregate<S>, S : State> storeOf(type: KClass<A>): StateStore<S>

    fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator

    fun idGeneratorOf(): IdGenerator = idGeneratorOf(Any::class)

    fun queryBus(): QueryBus

    fun commandBus(): CommandBus

    fun eventBus(): EventBus

    fun encryptor(): Encryptor

    fun faker(): Faker

    fun encryptor(cipherId: String, algorithm: String): Encryptor

    fun eventStreamOf(eventSourced: AbstractEventSourced<*>): EventStream = eventStreamOf(eventSourced, 0)

    fun eventStreamOf(eventSourced: AbstractEventSourced<*>, version: Int): EventStream

    fun eventConverterOf(event: Event): EventConverter<Event>

    fun eventConverterOf(type: String, variant: Int): EventConverter<Event>

    fun <T : Any> messageTranslatorOf(type: KClass<T>): MessageTranslator<T>

    fun <A : Aggregate<D>, D : State> snapshotStoreOf(type: KClass<A>): SnapshotStore<D>

    operator fun <T> invoke(block: InfrastructureContext.() -> T): T {
        return block.invoke(InfrastructureContext(this))
    }
}
