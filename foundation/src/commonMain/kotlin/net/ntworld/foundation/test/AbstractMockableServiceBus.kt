package net.ntworld.foundation.test

import net.ntworld.foundation.*
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.TestDsl
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.mocking.internal.CalledWithBuilderImpl
import net.ntworld.foundation.test.internal.BusCalledWithBuilderImpl
import net.ntworld.foundation.test.internal.ServiceBusCallFakeBuilderImpl
import kotlin.reflect.KClass

abstract class AbstractMockableServiceBus<T>(
    private val bus: T
) : MockableBus(), ServiceBus, LocalBusResolver<Request<*>, RequestHandler<*, *>>
    where T : ServiceBus, T : LocalBusResolver<Request<*>, RequestHandler<*, *>> {

    abstract fun guessRequestKClassByInstance(instance: Request<*>): KClass<out Request<*>>?

    val originalBus: ServiceBus = bus
    val originalProcess: (Request<*>, InvokeData) -> Unit = { request, _ ->
        bus.process(request)
    }

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
    @TestDsl.Mock
    infix fun <T : Request<R>, R : Response> whenProcessing(request: KClass<out T>): ServiceBusCallFakeBuilder.Start<T, R> {
        val start = (initMockInstanceForHandlerIfNeeded<Request<R>, R>(request) as HandlerManualMock<Request<R>, R>)
            .whenHandleCalled()

        return ServiceBusCallFakeBuilderImpl(start as CallFakeBuilderImpl<R>)
    }

    @TestDsl.Verify
    infix fun <T : Request<R>, R : Response> shouldProcess(request: KClass<out T>): BusCalledWithBuilder.Start<T> {
        val start = initMockInstanceForHandlerIfNeeded<Request<R>, R>(request).expectHandleCalled()

        return BusCalledWithBuilderImpl(start as CalledWithBuilderImpl)
    }
}