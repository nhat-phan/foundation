package net.ntworld.foundation.test

import net.ntworld.foundation.mocking.TestDsl
import kotlin.reflect.KClass

open class MockableBus {
    protected val handlerMocks = mutableMapOf<KClass<*>, HandlerManualMock<*, *>>()

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Any, R> initMockInstanceForHandlerIfNeeded(kClass: KClass<out T>): HandlerManualMock<*, *> {
        val mock = handlerMocks[kClass]
        if (null === mock) {
            handlerMocks[kClass] = HandlerManualMock<T, R>()
        }
        return handlerMocks[kClass] as HandlerManualMock<*, *>
    }

    @TestDsl.Verify
    fun verifyAll() {
        handlerMocks.values.forEach {
            it.verifyAll()
        }
    }
}