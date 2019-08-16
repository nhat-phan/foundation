package net.ntworld.foundation.internal.resolver

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.AggregateFactory
import net.ntworld.foundation.CannotResolveException
import kotlin.reflect.KClass

internal class AggregateFactoryResolver {
    private val typeFnMap = mutableMapOf<KClass<*>, () -> AggregateFactory<*>>()
    private val typeMap = mutableMapOf<KClass<*>, AggregateFactory<*>>()

    fun register(kClass: KClass<*>, fn: () -> AggregateFactory<*>) {
        typeFnMap[kClass] = fn
    }

    fun register(kClass: KClass<*>, converter: AggregateFactory<*>) {
        typeMap[kClass] = converter
    }

    fun canResolve(type: KClass<*>): Boolean {
        return typeFnMap.containsKey(type) || typeMap.containsKey(type)
    }

    fun <T : Aggregate> resolve(type: KClass<T>): AggregateFactory<T> {
        if (typeFnMap.containsKey(type)) {
            return typeFnMap[type]!!.invoke() as AggregateFactory<T>
        }

        if (typeMap.containsKey(type)) {
            return typeMap[type] as AggregateFactory<T>
        }
        throw CannotResolveException("AggregateFactoryResolver.resolve() cannot resolve ($type)")
    }
}