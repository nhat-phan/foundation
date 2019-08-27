package net.ntworld.foundation.internal.resolver

import net.ntworld.foundation.exception.CannotResolveException
import net.ntworld.foundation.MessageTranslator
import kotlin.reflect.KClass

internal class MessageTranslatorResolver {
    private val typeFnMap = mutableMapOf<KClass<*>, () -> MessageTranslator<*>>()
    private val typeMap = mutableMapOf<KClass<*>, MessageTranslator<*>>()

    fun register(kClass: KClass<*>, fn: () -> MessageTranslator<*>) {
        typeFnMap[kClass] = fn
    }

    fun register(kClass: KClass<*>, translator: MessageTranslator<*>) {
        typeMap[kClass] = translator
    }

    fun canResolve(type: KClass<*>): Boolean {
        return typeFnMap.containsKey(type) || typeMap.containsKey(type)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> resolve(type: KClass<T>): MessageTranslator<T> {
        if (typeFnMap.containsKey(type)) {
            return typeFnMap[type]!!.invoke() as MessageTranslator<T>
        }

        if (typeMap.containsKey(type)) {
            return typeMap[type] as MessageTranslator<T>
        }
        throw CannotResolveException("MessageTranslatorResolver.resolve() cannot resolve ($type)")
    }
}