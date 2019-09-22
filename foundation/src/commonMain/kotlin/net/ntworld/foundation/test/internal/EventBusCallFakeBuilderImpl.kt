package net.ntworld.foundation.test.internal

import net.ntworld.foundation.Event
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.test.EventBusCallFakeBuilder

internal class EventBusCallFakeBuilderImpl<T: Event>(
    private val callFakeBuilderImpl: CallFakeBuilderImpl<Unit>
) : EventBusCallFakeBuilder.Action<T>,
    EventBusCallFakeBuilder.Calls<T>,
    EventBusCallFakeBuilder.Chain<T>,
    EventBusCallFakeBuilder.Start<T> {
    override fun alwaysDoesNothing() = callFakeBuilderImpl.alwaysReturns(Unit)

    override fun alwaysThrows(throwable: Throwable) = callFakeBuilderImpl.alwaysThrows(throwable)

    @Suppress("UNCHECKED_CAST")
    override fun alwaysRuns(fakeImpl: (T, InvokeData) -> Unit) {
        callFakeBuilderImpl.alwaysRuns { params, invokeData ->
            fakeImpl(params[0] as T, invokeData)
        }
    }

    override fun doesNothing(): EventBusCallFakeBuilder.Chain<T> {
        callFakeBuilderImpl.returns(Unit)

        return this
    }

    override fun throws(throwable: Throwable): EventBusCallFakeBuilder.Chain<T> {
        callFakeBuilderImpl.throws(throwable)

        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun runs(fakeImpl: (T) -> Unit): EventBusCallFakeBuilder.Chain<T> {
        callFakeBuilderImpl.runs {
            fakeImpl(it[0] as T)
        }

        return this
    }

    override fun onCall(n: Int): EventBusCallFakeBuilder.Action<T> {
        callFakeBuilderImpl.onCall(n)

        return this
    }

    override fun otherwiseDoesNothing() = alwaysDoesNothing()

    override fun otherwiseThrows(throwable: Throwable) = alwaysThrows(throwable)

    override fun otherwiseRuns(fakeImpl: (T, InvokeData) -> Unit) = alwaysRuns(fakeImpl)
}