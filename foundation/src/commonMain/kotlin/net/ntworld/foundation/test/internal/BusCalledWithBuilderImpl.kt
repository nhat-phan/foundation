package net.ntworld.foundation.test.internal

import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.internal.CalledWithBuilderImpl
import net.ntworld.foundation.test.BusCalledWithBuilder

internal class BusCalledWithBuilderImpl<T>(
    private val calledWithBuilderImpl: CalledWithBuilderImpl
) : BusCalledWithBuilder.Action<T>,
    BusCalledWithBuilder.Calls<T>,
    BusCalledWithBuilder.Chain<T>,
    BusCalledWithBuilder.Start<T> {
    override fun atLeast(count: Int): BusCalledWithBuilder.Calls<T> {
        calledWithBuilderImpl.atLeast(count)

        return this
    }

    override fun exact(count: Int): BusCalledWithBuilder.Calls<T> {
        calledWithBuilderImpl.exact(count)

        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun alwaysMatch(verify: (T, InvokeData) -> Boolean) {
        calledWithBuilderImpl.alwaysMatch(verify = { params, invokeData ->
            verify(params[0] as T, invokeData)
        })
    }

    override fun onCall(n: Int): BusCalledWithBuilder.Action<T> {
        calledWithBuilderImpl.onCall(n)

        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun match(verify: (T) -> Boolean): BusCalledWithBuilder.Chain<T> {
        calledWithBuilderImpl.match(verify = { params ->
            verify(params[0] as T)
        })

        return this
    }

    override fun otherwiseMatch(verify: (T, InvokeData) -> Boolean) = alwaysMatch(verify)

}