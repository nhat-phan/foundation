package net.ntworld.foundation.test

import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.ManualMock

class HandlerManualMock<in T, R> : ManualMock() {
    fun handle(request: T): R {
        return mockFunction(this::handle, request)
    }

    fun setHandleFallbackIfNotMocked(realHandlerFallback: () -> R) {
        setFallbackIfNotMocked(this::handle, realHandlerFallback)
    }

    fun whenHandleCalled(): CallFakeBuilder.Start<R> {
        return this.whenFunctionCalled(this::handle)
    }

    fun expectHandleCalled(): CalledWithBuilder.Start {
        return this.expectFunctionCalled(this::handle)
    }
}