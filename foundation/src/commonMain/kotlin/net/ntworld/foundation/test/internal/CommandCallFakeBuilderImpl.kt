package net.ntworld.foundation.test.internal

import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.ParameterList
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.test.CommandCallFakeBuilder

internal class CommandCallFakeBuilderImpl(
    private val callFakeBuilderImpl: CallFakeBuilderImpl<Unit>
) : CommandCallFakeBuilder.Action,
    CommandCallFakeBuilder.Calls,
    CommandCallFakeBuilder.Chain,
    CommandCallFakeBuilder.Start {
    override fun alwaysDoesNothing() = callFakeBuilderImpl.alwaysReturns(Unit)

    override fun alwaysThrows(throwable: Throwable) = callFakeBuilderImpl.alwaysThrows(throwable)

    override fun run(fakeImpl: (ParameterList, InvokeData) -> Unit) = callFakeBuilderImpl.run(fakeImpl)

    override fun doesNothing(): CommandCallFakeBuilder.Chain {
        callFakeBuilderImpl.returns(Unit)

        return this
    }

    override fun throws(throwable: Throwable): CommandCallFakeBuilder.Chain {
        callFakeBuilderImpl.throws(throwable)

        return this
    }

    override fun onCall(n: Int): CommandCallFakeBuilder.Action {
        callFakeBuilderImpl.onCall(n)

        return this
    }

    override fun otherwiseDoesNothing() = callFakeBuilderImpl.alwaysReturns(Unit)

    override fun otherwiseThrows(throwable: Throwable) = callFakeBuilderImpl.alwaysThrows(throwable)
}