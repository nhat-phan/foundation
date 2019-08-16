package net.ntworld.foundation.internal.resolver

import net.ntworld.foundation.CannotResolveException
import net.ntworld.foundation.eventSourcing.Event
import net.ntworld.foundation.eventSourcing.EventConverter
import kotlin.reflect.KClass

internal class EventConverterResolver {
    private val typeFnMap = mutableMapOf<KClass<*>, () -> EventConverter<*>>()
    private val typeMap = mutableMapOf<KClass<*>, EventConverter<*>>()
    private val keyFnMap = mutableMapOf<String, () -> EventConverter<*>>()
    private val keyMap = mutableMapOf<String, EventConverter<*>>()

    fun register(kClass: KClass<out Event>, fn: () -> EventConverter<*>) {
        typeFnMap[kClass] = fn
    }

    fun register(kClass: KClass<out Event>, converter: EventConverter<*>) {
        typeMap[kClass] = converter
    }

    fun canResolve(event: Event): Boolean {
        return typeFnMap.containsKey(event::class) || typeMap.containsKey(event::class)
    }

    fun resolve(event: Event): EventConverter<Event> {
        if (typeFnMap.containsKey(event::class)) {
            return typeFnMap[event::class]!!.invoke() as EventConverter<Event>
        }

        if (typeMap.containsKey(event::class)) {
            return typeMap[event::class] as EventConverter<Event>
        }
        throw CannotResolveException("EventConverterResolver.resolve() cannot resolve ($event)")
    }

    fun register(type: String, variant: Int, fn: () -> EventConverter<*>) {
        keyFnMap[buildKey(type, variant)] = fn
    }

    fun register(type: String, variant: Int, converter: EventConverter<*>) {
        keyMap[buildKey(type, variant)] = converter
    }

    fun canResolve(type: String, variant: Int): Boolean {
        val key = buildKey(type, variant)
        return keyFnMap.containsKey(key) || keyMap.containsKey(key)
    }

    fun resolve(type: String, variant: Int): EventConverter<Event> {
        val key = buildKey(type, variant)
        if (keyFnMap.containsKey(key)) {
            return keyFnMap[key]!!.invoke() as EventConverter<Event>
        }

        if (keyMap.containsKey(key)) {
            return keyMap[key] as EventConverter<Event>
        }
        throw CannotResolveException("EventConverterResolver.resolve() cannot resolve ($type, $variant)")
    }

    companion object {
        internal fun buildKey(type: String, variant: Int) = "$type-$variant"
    }
}