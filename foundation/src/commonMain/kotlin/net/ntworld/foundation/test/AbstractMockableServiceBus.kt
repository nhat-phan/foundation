package net.ntworld.foundation.test

import net.ntworld.foundation.*
import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import kotlin.reflect.KClass

abstract class AbstractMockableServiceBus<T>(
    private val bus: T
) : ServiceBus, LocalBusResolver<Request<*>, RequestHandler<*, *>>
    where T : ServiceBus, T : LocalBusResolver<Request<*>, RequestHandler<*, *>> {
    private val mocks = mutableMapOf<KClass<*>, RequestHandlerManualMock<*, *>>()

    abstract fun guessRequestKClassByInstance(instance: Request<*>): KClass<out Request<*>>?

    @Suppress("UNCHECKED_CAST")
    override fun <R : Response> process(request: Request<R>): ServiceBusProcessResult<R> {
        val kClass = guessRequestKClassByInstance(request) ?: request::class
        val mock = mocks[kClass] as RequestHandlerManualMock<Request<R>, R>?
        if (null === mock) {
            return bus.process(request)
        }

        val realHandler = resolve(request) as RequestHandler<Request<R>, R>?
        if (null !== realHandler) {
            mock.setRealHandleFallbackIfNotMocked { realHandler.handle(request) }
        }
        return ServiceBusProcessResult.make((mock).handle(request))
    }

    override fun resolve(instance: Request<*>): RequestHandler<*, *>? = bus.resolve(instance)

    @Suppress("UNCHECKED_CAST")
    private fun initMockInstanceIfNeeded(kClass: KClass<out Request<*>>): RequestHandlerManualMock<*, *> {
        val mock = mocks[kClass]
        if (null === mock) {
            mocks[kClass] = RequestHandlerManualMock<Request<*>, Response>()
        }
        return mocks[kClass] as RequestHandlerManualMock<*, *>
    }

    @Suppress("UNCHECKED_CAST")
    infix fun <R : Response> whenProcessing(request: KClass<out Request<R>>): CallFakeBuilder.Start<R> {
        return (initMockInstanceIfNeeded(request) as RequestHandlerManualMock<Request<R>, R>).whenHandleCalled()
    }

    infix fun <R : Response> shouldProcess(request: KClass<out Request<R>>): CalledWithBuilder.Start {
        return initMockInstanceIfNeeded(request).expectHandleCalled()
    }

    fun verifyAll() {
        mocks.values.forEach {
            it.verifyAll()
        }
    }
}