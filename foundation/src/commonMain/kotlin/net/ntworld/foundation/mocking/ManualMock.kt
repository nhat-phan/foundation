package net.ntworld.foundation.mocking

import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.mocking.internal.CalledWithBuilderImpl
import net.ntworld.foundation.mocking.internal.MockedFunctionImpl
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
        val name = findNameOf(func)
        if (!data.contains(name)) {
            data[name] = MockedFunctionImpl<R>(name)
        }

        @Suppress("UNCHECKED_CAST")
        return data[name] as MockedFunction<R>
    }

    private fun<R> findNameOf(func: KFunction<R>): String = MockedFunctionImpl.getKeyedName(func)

    private fun <R> mockFunctionByName(name: String, vararg params: Any?): R {
        return initMockedFunction<R>(name).invoke(params.toList())
    }

    protected fun <R> initMockedFunction(name: String): MockedFunction<R> {
        if (!data.contains(name)) {
            data[name] = MockedFunctionImpl<R>(name)
        }
        @Suppress("UNCHECKED_CAST")
        return (data[name] as MockedFunction<R>)
    }

    protected fun <R> setFallbackIfNotMocked(func: KFunction<R>, fallback: () -> R) {
        val mockedFunction = initMockedFunction<R>(findNameOf(func))
        if (!mockedFunction.isMocked()) {
            mockedFunction.setFallback(fallback)
        }
    }

    protected fun <R> whenFunctionCalled(func: KFunction<R>): CallFakeBuilder.Start<R> {
        val builder = CallFakeBuilderImpl<R>()
        initMockedFunction<R>(findNameOf(func)).setCallFakeBuilder(builder)

        return builder
    }

    protected fun <R> expectFunctionCalled(func: KFunction<R>): CalledWithBuilder.Start {
        val builder = CalledWithBuilderImpl()
        initMockedFunction<R>(findNameOf(func)).setCalledWithBuilder(builder)

        return builder
    }

    protected fun <R> mockFunction(func: KFunction<R>, vararg params: Any?): R {
        return mockFunctionByName(findNameOf(func), *params)
    }
}