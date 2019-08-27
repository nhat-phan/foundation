package net.ntworld.foundation

import net.ntworld.foundation.eventSourcing.EventConverter
import net.ntworld.foundation.internal.resolver.AggregateFactoryResolver
import net.ntworld.foundation.internal.resolver.EventConverterResolver
import net.ntworld.foundation.internal.resolver.MessageTranslatorResolver
import kotlin.reflect.KClass

open class InfrastructureProvider : InfrastructureResolver() {
    private val aggregateFactoryResolver = AggregateFactoryResolver()
    private val eventConverterResolver = EventConverterResolver()
    private val messageTranslatorResolver = MessageTranslatorResolver()

    // -----------------------------------------------------------------------------------
    // Aggregate Factory
    // -----------------------------------------------------------------------------------

    fun registerFactory(kClass: KClass<*>, fn: () -> AggregateFactory<*, *>) {
        aggregateFactoryResolver.register(kClass, fn)
    }

    fun registerFactory(kClass: KClass<*>, converter: AggregateFactory<*, *>) {
        aggregateFactoryResolver.register(kClass, converter)
    }

    override fun <A : Aggregate<S>, S: State> factoryOf(type: KClass<A>): AggregateFactory<A, S> {
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
    // MessageTranslator
    // -----------------------------------------------------------------------------------

    fun registerMessageTranslator(kClass: KClass<*>, fn: () -> MessageTranslator<*>) {
        messageTranslatorResolver.register(kClass, fn)
    }

    fun registerMessageTranslator(kClass: KClass<*>, translator: MessageTranslator<*>) {
        messageTranslatorResolver.register(kClass, translator)
    }

    override fun <T : Any> messageTranslatorOf(type: KClass<T>): MessageTranslator<T> {
        if (messageTranslatorResolver.canResolve(type)) {
            return messageTranslatorResolver.resolve(type)
        }
        return super.messageTranslatorOf(type)
    }
}