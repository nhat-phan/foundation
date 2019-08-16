package net.ntworld.foundation

import net.ntworld.foundation.eventSourcing.Event
import net.ntworld.foundation.eventSourcing.EventConverter
import net.ntworld.foundation.internal.resolver.AggregateFactoryResolver
import net.ntworld.foundation.internal.resolver.EventConverterResolver
import net.ntworld.foundation.internal.resolver.MessageConverterResolver
import kotlin.reflect.KClass

open class InfrastructureProvider : InfrastructureResolver() {
    private val aggregateFactoryResolver = AggregateFactoryResolver()
    private val eventConverterResolver = EventConverterResolver()
    private val messageConverterResolver = MessageConverterResolver()

    // -----------------------------------------------------------------------------------
    // Factory
    // -----------------------------------------------------------------------------------

    fun registerFactory(kClass: KClass<*>, fn: () -> AggregateFactory<*>) {
        aggregateFactoryResolver.register(kClass, fn)
    }

    fun registerFactory(kClass: KClass<*>, converter: AggregateFactory<*>) {
        aggregateFactoryResolver.register(kClass, converter)
    }

    override fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A> {
        if (aggregateFactoryResolver.canResolve(type)) {
            return aggregateFactoryResolver.resolve(type)
        }
        return super.factoryOf(type)
    }

    // -----------------------------------------------------------------------------------
    // EventConverter
    // -----------------------------------------------------------------------------------

    fun registerEventConverter(kClass: KClass<out Event>, fn: () -> EventConverter<*>) {
        eventConverterResolver.register(kClass, fn)
    }

    fun registerEventConverter(kClass: KClass<out Event>, converter: EventConverter<*>) {
        eventConverterResolver.register(kClass, converter)
    }

    fun registerEventConverter(type: String, variant: Int, fn: () -> EventConverter<*>) {
        eventConverterResolver.register(type, variant, fn)
    }

    fun registerEventConverter(type: String, variant: Int, converter: EventConverter<*>) {
        eventConverterResolver.register(type, variant, converter)
    }

    override fun eventConverterOf(event: Event): EventConverter<Event> {
        if (eventConverterResolver.canResolve(event)) {
            return eventConverterResolver.resolve(event)
        }
        return super.eventConverterOf(event)
    }

    override fun eventConverterOf(type: String, variant: Int): EventConverter<Event> {
        if (eventConverterResolver.canResolve(type, variant)) {
            return eventConverterResolver.resolve(type, variant)
        }
        return super.eventConverterOf(type, variant)
    }

    // -----------------------------------------------------------------------------------
    // MessageConverter
    // -----------------------------------------------------------------------------------

    fun registerMessageConverter(kClass: KClass<*>, fn: () -> MessageConverter<*>) {
        messageConverterResolver.register(kClass, fn)
    }

    fun registerMessageConverter(kClass: KClass<*>, converter: MessageConverter<*>) {
        messageConverterResolver.register(kClass, converter)
    }

    override fun <T : Any> messageConverterOf(type: KClass<T>): MessageConverter<T> {
        if (messageConverterResolver.canResolve(type)) {
            return messageConverterResolver.resolve(type)
        }
        return super.messageConverterOf(type)
    }
}