package net.ntworld.foundation.test

import net.ntworld.foundation.Request
import net.ntworld.foundation.RequestHandler
import net.ntworld.foundation.Response
import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.ManualMock

class RequestHandlerManualMock<in T, R : Response> : ManualMock(), RequestHandler<T, R> where T : Request<out R> {
    override fun handle(request: T): R {
        return mockFunction(this::handle, request)
    }

    fun setRealHandleFallbackIfNotMocked(realHandlerFallback: () -> R) {
        setFallbackIfNotMocked(this::handle, realHandlerFallback)
    }

    fun whenHandleCalled(): CallFakeBuilder.Start<R> {
        return this.whenFunctionCalled(this::handle)
    }

    fun expectHandleCalled(): CalledWithBuilder.Start {
        return this.expectFunctionCalled(this::handle)
    }
}
