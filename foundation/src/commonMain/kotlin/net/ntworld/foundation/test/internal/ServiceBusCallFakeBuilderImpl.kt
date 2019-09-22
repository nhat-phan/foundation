package net.ntworld.foundation.test.internal

import net.ntworld.foundation.Request
import net.ntworld.foundation.Response
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.test.ServiceBusCallFakeBuilder

internal class ServiceBusCallFakeBuilderImpl<T : Request<R>, R : Response>(
    private val callFakeBuilderImpl: CallFakeBuilderImpl<R>
) : ServiceBusCallFakeBuilder.Action<T, R>,
    ServiceBusCallFakeBuilder.Calls<T, R>,
    ServiceBusCallFakeBuilder.Chain<T, R>,
    ServiceBusCallFakeBuilder.Start<T, R> {

    override fun alwaysReturns(result: R) = callFakeBuilderImpl.alwaysReturns(result)

    override fun alwaysThrows(throwable: Throwable) = callFakeBuilderImpl.alwaysThrows(throwable)

    @Suppress("UNCHECKED_CAST")
    override fun alwaysRuns(fakeImpl: (T, InvokeData) -> R) {
        return callFakeBuilderImpl.alwaysRuns { params, invokeData ->
            fakeImpl(params[0] as T, invokeData)
        }
    }

    override fun returns(result: R): ServiceBusCallFakeBuilder.Chain<T, R> {
        callFakeBuilderImpl.returns(result)

        return this
    }

    override fun throws(throwable: Throwable): ServiceBusCallFakeBuilder.Chain<T, R> {
        callFakeBuilderImpl.throws(throwable)

        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun runs(fakeImpl: (T) -> R): ServiceBusCallFakeBuilder.Chain<T, R> {
        callFakeBuilderImpl.runs {
            fakeImpl(it[0] as T)
        }

        return this
    }

    override fun onCall(n: Int): ServiceBusCallFakeBuilder.Action<T, R> {
        callFakeBuilderImpl.onCall(n)

        return this
    }

    override fun otherwiseReturns(result: R) = alwaysReturns(result)

    override fun otherwiseThrows(throwable: Throwable) = alwaysThrows(throwable)

    override fun otherwiseRuns(fakeImpl: (T, InvokeData) -> R) = alwaysRuns(fakeImpl)
}