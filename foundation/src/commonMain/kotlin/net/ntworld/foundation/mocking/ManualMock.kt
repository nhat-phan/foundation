package net.ntworld.foundation.mocking

import net.ntworld.foundation.mocking.internal.MockedFunction
import kotlin.reflect.KFunction

open class ManualMock {
    private val data: MutableMap<String, MockedFunction<*>> = hashMapOf()

    internal fun resetAll() {
        for (mockedFunctions in data.values) {
            mockedFunctions.reset()
        }
    }

    internal fun verifyAll() {
        for (mockedFunctions in data.values) {
            mockedFunctions.verify()
        }
    }

    internal fun <R> getMockedFunction(func: KFunction<R>): MockedFunction<R> {
        val name = MockedFunction.getKeyedName(func)
        if (!data.contains(name)) {
            data[name] = MockedFunction<R>(name)
        }

        @Suppress("UNCHECKED_CAST")
        return data[name] as MockedFunction<R>
    }

    private fun <R> initMockedFunction(name: String): MockedFunction<R> {
        if (!data.contains(name)) {
            data[name] = MockedFunction<R>(name)
        }
        @Suppress("UNCHECKED_CAST")
        return (data[name] as MockedFunction<R>)
    }

    private fun <R> mockFunctionByName(name: String, fallback: (() -> R)?, vararg params: Any?): R {
        val mockedFunction = initMockedFunction<R>(name)
        if (fallback !== null && !mockedFunction.isMocked()) {
            return fallback.invoke()
        }
        return mockedFunction.invoke(params.toList())
    }

    protected fun <R> whenFunctionCalled(func: KFunction<R>): CallFakeBuilder.Start<R> {
        TODO()
    }

    protected fun <R> mockFunction(func: KFunction<R>, vararg params: Any?): R {
        return mockFunctionByName(MockedFunction.getKeyedName(func), null, params)
    }

    protected fun <R> mockFunction(func: KFunction<R>, fallback: () -> R, vararg params: Any?): R {
        return mockFunctionByName(MockedFunction.getKeyedName(func), fallback, params)
    }
}