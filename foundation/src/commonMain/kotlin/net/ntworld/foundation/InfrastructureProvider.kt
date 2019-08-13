package net.ntworld.foundation

import kotlin.reflect.KClass

open class InfrastructureProvider(next: Infrastructure? = null) : Infrastructure {
    private var next: Infrastructure? = next

    override fun setNext(next: Infrastructure): Infrastructure {
        this.next = next

        return next
    }

    override fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A> {
        if (null !== next) {
            return next!!.factoryOf(type)
        }
        throw CannotResolveException("Infrastructure.factoryOf() cannot resolve $type")
    }

    override fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A> {
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

}