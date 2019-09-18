package net.ntworld.foundation.test

import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.ManualMock

class PublisherManualMock<T> : ManualMock() {
    fun publish(request: T) {
        return mockFunction(this::publish, request)
    }

    fun setPublisherFallbackIfNotMocked(realPublisherFallback: () -> Unit) {
        setFallbackIfNotMocked(this::publish, realPublisherFallback)
    }

    fun whenPublishCalled(): CallFakeBuilder.Start<Unit> {
        return this.whenFunctionCalled(this::publish)
    }

    fun expectPublishCalled(): CalledWithBuilder.Start {
        return this.expectFunctionCalled(this::publish)
    }
}