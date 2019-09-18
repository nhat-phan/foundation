package net.ntworld.foundation.test

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

    fun verifyAll() {
        handlerMocks.values.forEach {
            it.verifyAll()
        }
    }
}