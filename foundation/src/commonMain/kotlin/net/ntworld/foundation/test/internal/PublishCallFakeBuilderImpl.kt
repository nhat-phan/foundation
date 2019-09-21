package net.ntworld.foundation.test.internal

import net.ntworld.foundation.fluency.Word
import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.ParameterList
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.test.PublishCallFakeBuilder

internal class PublishCallFakeBuilderImpl(
    private val callFakeBuilderImpl: CallFakeBuilderImpl<Unit>
) :
    PublishCallFakeBuilder.Action,
    PublishCallFakeBuilder.Calls,
    PublishCallFakeBuilder.Chain,
    PublishCallFakeBuilder.Start,
    CallFakeBuilder.Build<Unit> {

    override fun alwaysDoesNothing() = callFakeBuilderImpl.alwaysReturns(Unit)

    override fun publishesReceivedEvent(): PublishCallFakeBuilder.Chain {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun publishes(word: Word.ReceivedEvent): PublishCallFakeBuilder.Chain {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doesNothing(): PublishCallFakeBuilder.Chain {
        callFakeBuilderImpl.returns(Unit)

        return this
    }

    override fun onCall(n: Int): PublishCallFakeBuilder.Action {
        callFakeBuilderImpl.returns(Unit)

        return this
    }

    override fun otherwisePublishesEvents() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun otherwiseDoesNothing() = callFakeBuilderImpl.otherwiseReturns(Unit)

    override fun toCallFake(): ((ParameterList, InvokeData) -> Unit)? = callFakeBuilderImpl.toCallFake()
}