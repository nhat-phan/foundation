package net.ntworld.foundation.mocking

import kotlin.reflect.KFunction

class VerifyBuilder(private val instance: ManualMock) {
    infix fun <R> KFunction<R>.called(value: Boolean) {
        if (value) {
            return instance.getMockedFunction(this).setCalledAtLeast(1)
        }
        instance.getMockedFunction(this).setCalledCount(0)
    }

    infix fun <R> KFunction<R>.called(count: Int) {
        instance.getMockedFunction(this).setCalledCount(count)
    }

    infix fun <R> KFunction<R>.calledWith(block: (ParameterList) -> Boolean) {
        instance.getMockedFunction(this).setCalledWith1(block)
    }

    infix fun <R> KFunction<R>.calledWith(block: (ParameterList, InvokeData) -> Boolean) {
        instance.getMockedFunction(this).setCalledWith2(block)
    }

    fun verify() {
        instance.verifyAll()
    }
}