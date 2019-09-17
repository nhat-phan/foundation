package net.ntworld.foundation.mocking.internal

import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.MockingException
import net.ntworld.foundation.mocking.ParameterList

internal class CallFakeBuilderImpl<R> :
    CallFakeBuilder.Start<R>,
    CallFakeBuilder.Action<R>,
    CallFakeBuilder.Build<R>,
    CallFakeBuilder.Calls<R>,
    CallFakeBuilder.Chain<R> {

    private data class Call<T>(
        val ordinal: Int,
        var returned: T?,
        var thrown: Throwable?,
        var hasReturned: Boolean = false,
        var shouldThrow: Boolean = false
    ) {
        fun invoke(): T {
            if (hasReturned) {
                return returned!!
            }

            if (shouldThrow) {
                throw thrown!!
            }

            throw MockingException("Please provide returned value via .returns() or use .throws() to throw an exception")
        }
    }

    private val data = mutableMapOf<Int, Call<R>>()
    private var fakedImpl: ((ParameterList, InvokeData) -> R)? = null
    private var currentOrdinal = 0

    override fun alwaysReturns(result: R) = setResult(GLOBAL_ORDINAL, result)

    override fun alwaysThrows(throwable: Throwable) = setThrown(GLOBAL_ORDINAL, throwable)

    override fun otherwiseReturns(result: R) = alwaysReturns(result)

    override fun otherwiseThrows(throwable: Throwable) = alwaysThrows(throwable)

    override fun run(fakeImpl: (ParameterList, InvokeData) -> R) {
        fakedImpl = fakeImpl
    }

    override fun returns(result: R): CallFakeBuilder.Chain<R> {
        setResult(currentOrdinal, result)

        return this
    }

    override fun throws(throwable: Throwable): CallFakeBuilder.Chain<R> {
        setThrown(currentOrdinal, throwable)

        return this
    }

    override fun onCall(n: Int): CallFakeBuilder.Action<R> {
        currentOrdinal = n

        return this
    }

    override fun toCallFake(): ((ParameterList, InvokeData) -> R)? {
        if (data.isEmpty() && null === fakedImpl) {
            return null
        }

        if (null !== fakedImpl) {
            return fakedImpl
        }

        return { _, invokedData ->
            val call = data[invokedData.ordinal]
            if (null !== call) {
                call.invoke()
            } else {
                val global = data[GLOBAL_ORDINAL]
                if (null !== global) {
                    global.invoke()
                } else {
                    throw MockingException("There is no global call fake. Please provide returned value via .otherwiseReturns() or use .otherwiseThrows() to throw an exception")
                }
            }
        }
    }

    private fun setResult(ordinal: Int, result: R) {
        val call = findCall(ordinal)
        call.returned = result
        call.hasReturned = true
    }

    private fun setThrown(ordinal: Int, throwable: Throwable) {
        val call = findCall(ordinal)
        call.thrown = throwable
        call.shouldThrow = true
    }

    private fun findCall(ordinal: Int): Call<R> {
        val item = data[ordinal]
        if (item === null) {
            val new = Call<R>(ordinal, null, null)
            data[ordinal] = new
            return new
        }
        return item
    }

    companion object {
        const val GLOBAL_ORDINAL = -1
    }
}