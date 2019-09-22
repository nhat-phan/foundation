package net.ntworld.foundation.test.internal

import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.test.QueryBusCallFakeBuilder

internal class QueryBusCallFakeBuilderImpl<Q : Query<R>, R : QueryResult>(
    private val callFakeBuilderImpl: CallFakeBuilderImpl<R>
) : QueryBusCallFakeBuilder.Action<Q, R>,
    QueryBusCallFakeBuilder.Calls<Q, R>,
    QueryBusCallFakeBuilder.Chain<Q, R>,
    QueryBusCallFakeBuilder.Start<Q, R> {

    override fun alwaysReturns(result: R) = callFakeBuilderImpl.alwaysReturns(result)

    override fun alwaysThrows(throwable: Throwable) = callFakeBuilderImpl.alwaysThrows(throwable)

    @Suppress("UNCHECKED_CAST")
    override fun alwaysRuns(fakeImpl: (Q, InvokeData) -> R) {
        return callFakeBuilderImpl.alwaysRuns { params, invokeData ->
            fakeImpl(params[0] as Q, invokeData)
        }
    }

    override fun returns(result: R): QueryBusCallFakeBuilder.Chain<Q, R> {
        callFakeBuilderImpl.returns(result)

        return this
    }

    override fun throws(throwable: Throwable): QueryBusCallFakeBuilder.Chain<Q, R> {
        callFakeBuilderImpl.throws(throwable)

        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun runs(fakeImpl: (Q) -> R): QueryBusCallFakeBuilder.Chain<Q, R> {
        callFakeBuilderImpl.runs {
            fakeImpl(it[0] as Q)
        }

        return this
    }

    override fun onCall(n: Int): QueryBusCallFakeBuilder.Action<Q, R> {
        callFakeBuilderImpl.onCall(n)

        return this
    }

    override fun otherwiseReturns(result: R) = alwaysReturns(result)

    override fun otherwiseThrows(throwable: Throwable) = alwaysThrows(throwable)

    override fun otherwiseRuns(fakeImpl: (Q, InvokeData) -> R) = alwaysRuns(fakeImpl)
}