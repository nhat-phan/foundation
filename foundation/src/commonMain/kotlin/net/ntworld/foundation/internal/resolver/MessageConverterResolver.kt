package net.ntworld.foundation.internal.resolver

import net.ntworld.foundation.exception.CannotResolveException
import net.ntworld.foundation.MessageConverter
import kotlin.reflect.KClass

internal class MessageConverterResolver {
    private val typeFnMap = mutableMapOf<KClass<*>, () -> MessageConverter<*>>()
    private val typeMap = mutableMapOf<KClass<*>, MessageConverter<*>>()

    fun register(kClass: KClass<*>, fn: () -> MessageConverter<*>) {
        typeFnMap[kClass] = fn
    }

    fun register(kClass: KClass<*>, converter: MessageConverter<*>) {
        typeMap[kClass] = converter
    }

    fun canResolve(type: KClass<*>): Boolean {
        return typeFnMap.containsKey(type) || typeMap.containsKey(type)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> resolve(type: KClass<T>): MessageConverter<T> {
        if (typeFnMap.containsKey(type)) {
            return typeFnMap[type]!!.invoke() as MessageConverter<T>
        }

        if (typeMap.containsKey(type)) {
            return typeMap[type] as MessageConverter<T>
        }
        throw CannotResolveException("MessageConverterResolver.resolve() cannot resolve ($type)")
    }
}