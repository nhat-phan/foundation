package net.ntworld.foundation.test

import net.ntworld.foundation.Event
import net.ntworld.foundation.EventBus
import net.ntworld.foundation.EventHandler
import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.TestDsl
import kotlin.reflect.KClass

abstract class AbstractMockableEventBus<T>(
    private val bus: T
) : MockableBus(), EventBus, LocalBusResolver<Event, EventHandler<*>>
    where T : EventBus, T : LocalBusResolver<Event, EventHandler<*>> {
    private val publisherMocks = mutableMapOf<KClass<*>, PublisherManualMock<*>>()

    abstract fun guessEventKClassByInstance(instance: Event): KClass<out Event>?

    val originalBus: EventBus = bus

    override fun publish(event: Event) {
        val kClass = guessEventKClassByInstance(event) ?: event::class
        val mock = publisherMocks[kClass] as PublisherManualMock<Event>?
        if (null === mock) {
            return bus.publish(event)
        }

        mock.setPublisherFallbackIfNotMocked {}
        mock.publish(event)
    }

    override fun process(event: Event) {
        val kClass = guessEventKClassByInstance(event) ?: event::class
        val mock = handlerMocks[kClass] as HandlerManualMock<Event, Unit>?
        if (null === mock) {
            return bus.process(event)
        }

        val realHandler = resolve(event) as EventHandler<Event>?
        if (null !== realHandler) {
            mock.setHandleFallbackIfNotMocked { realHandler.handle(event) }
        }
        mock.handle(event)
    }

    override fun resolve(instance: Event) = bus.resolve(instance)

    @Suppress("UNCHECKED_CAST")
    @TestDsl.Mock
    infix fun whenProcessing(event: KClass<out Event>): CallFakeBuilder.Start<Unit> {
        return (initMockInstanceForHandlerIfNeeded<Event, Unit>(event) as HandlerManualMock<Event, Unit>).whenHandleCalled()
    }

    @TestDsl.Verify
    infix fun shouldProcess(event: KClass<out Event>): CalledWithBuilder.Start {
        return initMockInstanceForHandlerIfNeeded<Event, Unit>(event).expectHandleCalled()
    }
//
//    infix fun whenPublishing(event: KClass<out Event>): CallFakeBuilder.Start<Unit> {
//
//    }
}