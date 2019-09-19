package net.ntworld.foundation.mocking

interface MockedFunction<R> {
    fun reset()

    fun verify()

    fun isMocked(): Boolean

    fun invoke(params: List<Any?>): R

    fun setFallback(fallback: () -> R)

    fun setResult(result: R)

    fun setCallFake(callFake: (ParameterList) -> R)

    fun setCallFake(callFake: (ParameterList, InvokeData) -> R)

    fun setCallFakeBuilder(callFakeBuilder: CallFakeBuilder.Build<R>)

    fun setCalledAtLeast(count: Int)

    fun setCalledCount(count: Int)

    fun setCalledWith1(block: (ParameterList) -> Boolean)

    fun setCalledWith2(block: (ParameterList, InvokeData) -> Boolean)

    fun setCalledWithBuilder(calledWithBuilder: CalledWithBuilder.Build)
}