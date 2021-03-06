package net.ntworld.foundation.mocking.internal

import net.ntworld.foundation.mocking.*
import kotlin.reflect.KFunction

internal class MockedFunctionImpl<R>(private val fnName: String) : MockedFunction<R> {
    private var fallback: (() -> R)? = null
    private var hasResult: Boolean = false
    private var result: Any? = null
    private var callFake1: ((ParameterList) -> R)? = null
    private var callFake2: (Invoker<R>)? = null
    private var callFakeBuilder: CallFakeBuilder.Build<R>? = null

    private var calledAtLeast: Int = -1
    private var calledCount: Int = -1
    private var calledWith1: ((ParameterList) -> Boolean)? = null
    private var calledWith2: ((ParameterList, InvokeData) -> Boolean)? = null
    private var calledWithBuilder: CalledWithBuilder.Build? = null

    private val calls: FunctionCalls = FunctionCalls()

    override fun reset() {
        this.hasResult = false
        this.result = null
        this.callFake1 = null
        this.callFake2 = null
        this.callFakeBuilder = null

        this.calledCount = -1
        this.calledAtLeast = -1
        this.calledWith1 = null
        this.calledWith2 = null
        this.calledWithBuilder = null

        this.calls.reset()
    }

    override fun verify() {
        val builder = calledWithBuilder
        if (null !== builder) {
            this.calledCount = builder.getCalledCount()
            this.calledAtLeast = builder.getCalledAtLeast()
            this.calledWith2 = builder.toCalledWith()
        }

        if (calledCount != -1 && calledCount != calls.count()) {
            throw MockingException("Expect function $fnName called $calledCount time(s) but it actually called ${calls.count()} time(s).")
        }

        if (calledAtLeast != -1 && calledAtLeast > calls.count()) {
            throw MockingException("Expect function $fnName called at least $calledAtLeast time(s) but it actually called ${calls.count()} time(s).")
        }

        if (null !== calledWith2 && !calls.verify(calledWith2!!)) {
            throw MockingException("Expect function $fnName called with params is failed.")
        }

        if (null !== calledWith1 && !calls.verify(calledWith1!!)) {
            throw MockingException("Expect function $fnName called with params is failed.")
        }

        this.reset()
    }

    override fun isMocked(): Boolean {
        return null !== callFakeBuilder || null !== callFake2 || null !== callFake1 || hasResult
    }

    override fun invoke(params: List<Any?>): R {
        val fallbackFn = fallback
        if (null !== fallbackFn) {
            return calls.returnResult(params, fallbackFn.invoke())
        }

        val builder = callFakeBuilder
        if (null !== builder) {
            this.callFake2 = builder.toCallFake()
        }

        if (null !== callFake2) {
            return calls.callFake(params, callFake2!!)
        }

        if (null !== callFake1) {
            return calls.callFake(params, callFake1!!)
        }

        if (hasResult) {
            @Suppress("UNCHECKED_CAST")
            return calls.returnResult(params, result as R)
        }

        throw MockingException("Could not invoke a mocking function, please use mock(...) to set a result or callFake first")
    }

    override fun setFallback(fallback: () -> R) {
        this.fallback = fallback
    }

    override fun setResult(result: R) {
        this.hasResult = true
        this.result = result
    }

    override fun setCallFake(callFake: (ParameterList) -> R) {
        this.callFake1 = callFake
    }

    override fun setCallFake(callFake: Invoker<R>) {
        this.callFake2 = callFake
    }

    override fun setCallFakeBuilder(callFakeBuilder: CallFakeBuilder.Build<R>) {
        this.callFakeBuilder = callFakeBuilder
    }

    override fun setCalledAtLeast(count: Int) {
        this.calledAtLeast = count
    }

    override fun setCalledCount(count: Int) {
        this.calledCount = count
    }

    override fun setCalledWith1(block: (ParameterList) -> Boolean) {
        this.calledWith1 = block
    }

    override fun setCalledWith2(block: (ParameterList, InvokeData) -> Boolean) {
        this.calledWith2 = block
    }

    override fun setCalledWithBuilder(calledWithBuilder: CalledWithBuilder.Build) {
        this.calledWithBuilder = calledWithBuilder
    }

    companion object {
        fun getKeyedName(func: KFunction<*>): String {
            return func.name
        }
    }
}