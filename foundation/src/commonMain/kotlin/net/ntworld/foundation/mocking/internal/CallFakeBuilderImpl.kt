package net.ntworld.foundation.mocking.internal

import net.ntworld.foundation.mocking.*

internal class CallFakeBuilderImpl<R> :
    CallFakeBuilder.Start<R>,
    CallFakeBuilder.Action<R>,
    CallFakeBuilder.Build<R>,
    CallFakeBuilder.Calls<R>,
    CallFakeBuilder.Chain<R> {

    private data class Call<T>(
        val ordinal: Int,
        var fakeImpl: Invoker<T>?,
        var returned: T?,
        var thrown: Throwable?,
        var runFakeImpl: Boolean = false,
        var hasReturned: Boolean = false,
        var shouldThrow: Boolean = false
    ) {
        fun invoke(list: ParameterList, invokeData: InvokeData): T {
            if (runFakeImpl) {
                return fakeImpl!!.invoke(list, invokeData)
            }

            if (hasReturned) {
                return returned!!
            }

            if (shouldThrow) {
                throw thrown!!
            }

            throw MockingException("Please provide returned value via .returns() or use .throws() to throw an exception or .runs() to provide fake implementation")
        }
    }

    private val data = mutableMapOf<Int, Call<R>>()
    private var currentOrdinal = 0

    override fun alwaysReturns(result: R) = setResult(GLOBAL_ORDINAL, result)

    override fun alwaysThrows(throwable: Throwable) = setThrown(GLOBAL_ORDINAL, throwable)

    override fun alwaysRuns(fakeImpl: Invoker<R>) = setFakeImpl(GLOBAL_ORDINAL, fakeImpl)

    override fun otherwiseReturns(result: R) = alwaysReturns(result)

    override fun otherwiseThrows(throwable: Throwable) = alwaysThrows(throwable)

    override fun otherwiseRuns(fakeImpl: Invoker<R>) = alwaysRuns(fakeImpl)

    override fun returns(result: R): CallFakeBuilder.Chain<R> {
        setResult(currentOrdinal, result)

        return this
    }

    override fun throws(throwable: Throwable): CallFakeBuilder.Chain<R> {
        setThrown(currentOrdinal, throwable)

        return this
    }

    override fun runs(fakeImpl: (ParameterList) -> R): CallFakeBuilder.Chain<R> {
        setFakeImpl(currentOrdinal) { list, _ ->
            fakeImpl(list)
        }

        return this
    }

    override fun onCall(n: Int): CallFakeBuilder.Action<R> {
        currentOrdinal = n

        return this
    }

    override fun toCallFake(): (Invoker<R>)? {
        if (data.isEmpty()) {
            return null
        }

        return { list, invokedData ->
            val call = data[invokedData.callIndex]
            if (null !== call) {
                call.invoke(list, invokedData)
            } else {
                val global = data[GLOBAL_ORDINAL]
                if (null !== global) {
                    global.invoke(list, invokedData)
                } else {
                    throw MockingException("There is no global call fake. Please provide returned value via .otherwiseReturns() or use .otherwiseThrows() to throw an exception or .otherwiseRuns() to provide fake implementation")
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

    private fun setFakeImpl(ordinal: Int, fakedImpl: Invoker<R>) {
        val call = findCall(ordinal)
        call.fakeImpl = fakedImpl
        call.runFakeImpl = true
    }

    private fun findCall(ordinal: Int): Call<R> {
        val item = data[ordinal]
        if (item === null) {
            val new = Call<R>(ordinal, null, null, null)
            data[ordinal] = new
            return new
        }
        return item
    }

    companion object {
        const val GLOBAL_ORDINAL = -1
    }
}