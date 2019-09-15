package net.ntworld.foundation.mocking

interface CallFakeBuilder {
    interface Start<R> : Calls<R> {
        infix fun alwaysReturns(result: R)

        infix fun alwaysThrows(throwable: Throwable)

        infix fun run(fakeImpl: (ParameterList, InvokeData) -> R)
    }

    interface Action<R> {
        infix fun returns(result: R): Chain<R>

        infix fun throws(throwable: Throwable): Chain<R>
    }

    interface Calls<R> {
        infix fun onCall(n: Int): Action<R>

        infix fun onFirstCallReturns(result: R) = onCall(0).returns(result)

        infix fun onFirstCallThrows(throwable: Throwable) = onCall(0).throws(throwable)

        infix fun onSecondCallReturns(result: R) = onCall(1).returns(result)

        infix fun onSecondCallThrows(throwable: Throwable) = onCall(1).throws(throwable)

        infix fun onThirdCallReturns(result: R) = onCall(2).returns(result)

        infix fun onThirdCallThrows(throwable: Throwable) = onCall(2).throws(throwable)
    }

    interface Chain<R> : Calls<R> {
        infix fun otherwiseReturns(result: R)

        infix fun otherwiseThrows(throwable: Throwable)
    }

    interface Build<R> {
        fun toCallFake(): ((ParameterList, InvokeData) -> R)?
    }
}
