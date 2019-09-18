package net.ntworld.foundation.test

import net.ntworld.foundation.*
import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import kotlin.reflect.KClass

abstract class AbstractMockableServiceBus<T>(
    private val bus: T
) : MockableBus(), ServiceBus, LocalBusResolver<Request<*>, RequestHandler<*, *>>
    where T : ServiceBus, T : LocalBusResolver<Request<*>, RequestHandler<*, *>> {

    abstract fun guessRequestKClassByInstance(instance: Request<*>): KClass<out Request<*>>?

    @Suppress("UNCHECKED_CAST")
    override fun <R : Response> process(request: Request<R>): ServiceBusProcessResult<R> {
        val kClass = guessRequestKClassByInstance(request) ?: request::class
        val mock = handlerMocks[kClass] as HandlerManualMock<Request<R>, R>?
        if (null === mock) {
            return bus.process(request)
        }

        val realHandler = resolve(request) as RequestHandler<Request<R>, R>?
        if (null !== realHandler) {
            mock.setHandleFallbackIfNotMocked { realHandler.handle(request) }
        }
        return ServiceBusProcessResult.make((mock).handle(request))
    }

    override fun resolve(instance: Request<*>) = bus.resolve(instance)

    @Suppress("UNCHECKED_CAST")
    infix fun <R : Response> whenProcessing(request: KClass<out Request<R>>): CallFakeBuilder.Start<R> {
        return (initMockInstanceForHandlerIfNeeded<Request<R>, R>(request) as HandlerManualMock<Request<R>, R>).whenHandleCalled()
    }

    infix fun <R : Response> shouldProcess(request: KClass<out Request<R>>): CalledWithBuilder.Start {
        return initMockInstanceForHandlerIfNeeded<Request<R>, R>(request).expectHandleCalled()
    }
}