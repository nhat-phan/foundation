package net.ntworld.foundation.mocking

import kotlin.reflect.KFunction

class MockBuilder(private val instance: ManualMock) {
    infix fun <R> KFunction<R>.callFake(fakeImplementation: (params: ParameterList) -> R) {
        instance.getMockedFunction(this).setCallFake(fakeImplementation)
    }

    infix fun <R> KFunction<R>.callFake(fakeImplementation: (params: ParameterList, invokeData: InvokeData) -> R) {
        instance.getMockedFunction(this).setCallFake(fakeImplementation)
    }

    infix fun <R> KFunction<R>.willReturn(result: R) {
        instance.getMockedFunction(this).setResult(result)
    }

    infix fun <R> KFunction<R>.willReturn(block: () -> R) {
        instance.getMockedFunction(this).setResult(block())
    }
}