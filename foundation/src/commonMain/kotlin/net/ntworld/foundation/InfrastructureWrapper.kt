package net.ntworld.foundation

import kotlin.reflect.KClass

open class InfrastructureWrapper(private val base: Infrastructure) : Infrastructure {
    override fun setNext(next: Infrastructure): Infrastructure {
        if (this.base is InfrastructureProvider) {
            return this.base.setNext(next)
        }
        throw CannotSetInfrastructureProviderChain()
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
}