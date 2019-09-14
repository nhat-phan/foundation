package net.ntworld.foundation.mocking.internal

import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.MockingException
import net.ntworld.foundation.mocking.ParameterList

class CallFakeBuilderImpl<R> {
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
    private var currentOrdinal = 0

    private fun findCall(ordinal: Int): Call<R> {
        val item = data[ordinal]
        if (item === null) {
            val new = Call<R>(ordinal, null, null)
            data[ordinal] = new
            return new
        }
        return item
    }

    fun throws(exception: Throwable): CallFakeBuilderImpl<R> {
        val call = findCall(currentOrdinal)
        call.thrown = exception
        call.shouldThrow = true

        return this
    }

    fun returns(result: R): CallFakeBuilderImpl<R> {
        val call = findCall(currentOrdinal)
        call.returned = result
        call.hasReturned = true

        return this
    }

    fun onCall(n: Int): CallFakeBuilderImpl<R> {
        currentOrdinal = n

        return this
    }

    fun toCallFake(): ((ParameterList, InvokeData) -> R)? {
        return { list, invokedData ->
            val call = data[invokedData.ordinal]
            if (null !== call) {
                call.invoke()
            } else {
                throw MockingException("Please provide returned value via .returns() or use .throws() to throw an exception")
            }
        }
    }

    fun willReturn(result: R) = returns(result)

    fun willThrowException(exception: Throwable) = throws(exception)

    fun onFirstCall() = onCall(0)

    fun onSecondCall() = onCall(1)

    fun onThirdCall() = onCall(2)
}