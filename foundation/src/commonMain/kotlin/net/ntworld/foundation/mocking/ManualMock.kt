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
            data[name] = MockedFunction(func)
        }

        @Suppress("UNCHECKED_CAST")
        return data[name] as MockedFunction<R>
    }

    protected fun <R> mockFunction(func: KFunction<R>, vararg params: Any): R {
        val name = MockedFunction.getKeyedName(func)
        if (!data.contains(name)) {
            data[name] = MockedFunction(func)
        }

        @Suppress("UNCHECKED_CAST")
        return (data[name] as MockedFunction<R>).invoke(params.toList())
    }
}