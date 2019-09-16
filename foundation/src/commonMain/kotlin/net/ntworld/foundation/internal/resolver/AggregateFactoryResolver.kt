package net.ntworld.foundation.internal.resolver

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.AggregateFactory
import net.ntworld.foundation.exception.CannotResolveException
import net.ntworld.foundation.State
import kotlin.reflect.KClass

internal class AggregateFactoryResolver {
    private val typeFnMap = mutableMapOf<KClass<*>, () -> AggregateFactory<*, *>>()
    private val typeMap = mutableMapOf<KClass<*>, AggregateFactory<*, *>>()

    fun register(kClass: KClass<*>, fn: () -> AggregateFactory<*, *>) {
        typeFnMap[kClass] = fn
    }

    fun register(kClass: KClass<*>, converter: AggregateFactory<*, *>) {
        typeMap[kClass] = converter
    }

    fun canResolve(type: KClass<*>): Boolean {
        return typeFnMap.containsKey(type) || typeMap.containsKey(type)
    }

    @Suppress("UNCHECKED_CAST")
    fun <A : Aggregate<S>, S : State> resolve(type: KClass<A>): AggregateFactory<A, S> {
        if (typeFnMap.containsKey(type)) {
            return typeFnMap[type]!!.invoke() as AggregateFactory<A, S>
        }

        if (typeMap.containsKey(type)) {
            return typeMap[type] as AggregateFactory<A, S>
        }
        throw CannotResolveException("AggregateFactoryResolver.resolve() cannot resolve ($type)")
    }
}