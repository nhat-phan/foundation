package net.ntworld.foundation.cqrs

import net.ntworld.foundation.*
import kotlin.reflect.KClass

open class InfrastructureCommandHandlerContext(open val self: Infrastructure) {
    @InfrastructureDsl.CommandHandlerDsl
    fun environment(): Environment = self.environment()

    @InfrastructureDsl.CommandHandlerDsl
    fun <A : Aggregate<S>, S : State> factoryOf(type: KClass<A>): AggregateFactory<A, S> = self.factoryOf(type)

    @InfrastructureDsl.CommandHandlerDsl
    fun <T : ReceivedData<Q, R>, Q : Query<R>, R : QueryResult> receiverOf(type: KClass<T>): DataReceiver<T> =
        self.receiverOf(type)

    @InfrastructureDsl.CommandHandlerDsl
    fun <A : Aggregate<S>, S : State> storeOf(type: KClass<A>): StateStore<S> = self.storeOf(type)

    @InfrastructureDsl.CommandHandlerDsl
    fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator = self.idGeneratorOf(type)

    @InfrastructureDsl.CommandHandlerDsl
    fun idGeneratorOf(): IdGenerator = self.idGeneratorOf()

    @InfrastructureDsl.CommandHandlerDsl
    fun commandBus(): CommandBus = self.commandBus()

    @InfrastructureDsl.CommandHandlerDsl
    fun eventBus(): EventBus = self.eventBus()

    @InfrastructureDsl.CommandHandlerDsl
    inline fun <reified A : Aggregate<S>, S : State> save(instance: A) {
        self.storeOf(A::class).save(instance.state)
    }
}
