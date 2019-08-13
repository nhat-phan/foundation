package net.ntworld.foundation

import kotlin.reflect.KClass


interface Infrastructure {
    fun setNext(next: Infrastructure): Infrastructure

    fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A>

    fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A>

    fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator

    fun idGeneratorOf(): IdGenerator = idGeneratorOf(Any::class)

    operator fun <T> invoke(block: InfrastructureContext.() -> T): T {
        return block.invoke(InfrastructureContext(this))
    }
}
