package net.ntworld.foundation.test

import kotlin.reflect.KClass

open class MockableBus {
    protected val mocks = mutableMapOf<KClass<*>, HandlerManualMock<*, *>>()

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Any, R> initMockInstanceIfNeeded(kClass: KClass<out T>): HandlerManualMock<*, *> {
        val mock = mocks[kClass]
        if (null === mock) {
            mocks[kClass] = HandlerManualMock<T, R>()
        }
        return mocks[kClass] as HandlerManualMock<*, *>
    }

    fun verifyAll() {
        mocks.values.forEach {
            it.verifyAll()
        }
    }
}