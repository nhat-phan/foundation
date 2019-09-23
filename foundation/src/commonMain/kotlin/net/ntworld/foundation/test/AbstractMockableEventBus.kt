package net.ntworld.foundation.test

import net.ntworld.foundation.Event
import net.ntworld.foundation.EventBus
import net.ntworld.foundation.EventHandler
import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.TestDsl
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.test.internal.EventBusCallFakeBuilderImpl
import kotlin.reflect.KClass

abstract class AbstractMockableEventBus<T>(
    private val bus: T
) : MockableBus(), EventBus, LocalBusResolver<Event, Array<EventHandler<*>>>
    where T : EventBus, T : LocalBusResolver<Event, Array<EventHandler<*>>> {
    private val publisherMocks = mutableMapOf<KClass<*>, PublisherManualMock<*>>()

    abstract fun guessEventKClassByInstance(instance: Event): KClass<out Event>?

    val originalBus: EventBus = bus
    val originalProcess: (Event, InvokeData) -> Unit = { event, _ ->
        bus.process(event)
    }
    val originalPublish: (Event, InvokeData) -> Unit = { event, _ ->
        bus.publish(event)
    }

    @Suppress("UNCHECKED_CAST")
    override fun publish(event: Event) {
        val kClass = guessEventKClassByInstance(event) ?: event::class
        val mock = publisherMocks[kClass] as PublisherManualMock<Event>?
        if (null === mock) {
            return bus.publish(event)
        }

        mock.setPublisherFallbackIfNotMocked { bus.publish(event) }
        mock.publish(event)
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(event: Event) {
        val kClass = guessEventKClassByInstance(event) ?: event::class
        val mock = handlerMocks[kClass] as HandlerManualMock<Event, Unit>?
        if (null === mock) {
            return bus.process(event)
        }

        val realHandlers = resolve(event) as Array<EventHandler<Event>>?
        if (null !== realHandlers) {
            mock.setHandleFallbackIfNotMocked { realHandlers.forEach { it.handle(event) } }
        }
        mock.handle(event)
    }

    override fun resolve(instance: Event) = bus.resolve(instance)

    @Suppress("UNCHECKED_CAST")
    @TestDsl.Mock
    infix fun <T : Event> whenProcessing(event: KClass<out T>): EventBusCallFakeBuilder.Start<T> {
        val start = (initMockInstanceForHandlerIfNeeded<T, Unit>(event) as HandlerManualMock<T, Unit>)
            .whenHandleCalled()

        return EventBusCallFakeBuilderImpl(start as CallFakeBuilderImpl<Unit>)
    }

    @TestDsl.Verify
    infix fun shouldProcess(event: KClass<out Event>): CalledWithBuilder.Start {
        return initMockInstanceForHandlerIfNeeded<Event, Unit>(event).expectHandleCalled()
    }

    @Suppress("UNCHECKED_CAST")
    @TestDsl.Mock
    infix fun <T : Event> whenPublishing(event: KClass<out T>): EventBusCallFakeBuilder.Start<T> {
        val start = (initMockInstanceForPublisherIfNeeded(event) as PublisherManualMock<T>)
            .whenPublishCalled()

        return EventBusCallFakeBuilderImpl(start as CallFakeBuilderImpl<Unit>)
    }

    @TestDsl.Verify
    infix fun shouldPublish(event: KClass<out Event>): CalledWithBuilder.Start {
        return initMockInstanceForPublisherIfNeeded(event).expectPublishCalled()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> initMockInstanceForPublisherIfNeeded(kClass: KClass<out T>): PublisherManualMock<*> {
        val mock = publisherMocks[kClass]
        if (null === mock) {
            publisherMocks[kClass] = PublisherManualMock<T>()
        }
        return publisherMocks[kClass] as PublisherManualMock<*>
    }

    @TestDsl.Verify
    fun verifyPublishCalledAsExpected() {
        publisherMocks.values.forEach {
            it.verifyAll()
        }
    }

    @TestDsl.Verify
    override fun verifyAll() {
        verifyProcessCalledAsExpected()
        verifyPublishCalledAsExpected()
    }
}