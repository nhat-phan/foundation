package net.ntworld.foundation.mocking

import net.ntworld.foundation.fluency.Word

interface CallFakeBuilder {
    interface Start<R> : Calls<R> {
        @TestDsl.Mock
        infix fun alwaysReturns(result: R)

        @TestDsl.Mock
        infix fun alwaysThrows(throwable: Throwable)

        @TestDsl.Mock
        infix fun run(fakeImpl: (ParameterList, InvokeData) -> R)
    }

    interface Action<R> {
        @TestDsl.Mock
        infix fun returns(result: R): Chain<R>

        @TestDsl.Mock
        infix fun throws(throwable: Throwable): Chain<R>
    }

    interface Calls<R> {
        @TestDsl.Mock
        infix fun onCall(n: Int): Action<R>

        @TestDsl.Mock
        infix fun on(nth: Word.ForOnCallFake): Action<R> = onCall(nth.value)

        @TestDsl.Mock
        infix fun onFirstCallReturns(result: R) = onCall(0).returns(result)

        @TestDsl.Mock
        infix fun onFirstCallThrows(throwable: Throwable) = onCall(0).throws(throwable)

        @TestDsl.Mock
        infix fun onSecondCallReturns(result: R) = onCall(1).returns(result)

        @TestDsl.Mock
        infix fun onSecondCallThrows(throwable: Throwable) = onCall(1).throws(throwable)

        @TestDsl.Mock
        infix fun onThirdCallReturns(result: R) = onCall(2).returns(result)

        @TestDsl.Mock
        infix fun onThirdCallThrows(throwable: Throwable) = onCall(2).throws(throwable)
    }

    interface Chain<R> : Calls<R> {
        @TestDsl.Mock
        infix fun otherwiseReturns(result: R)

        @TestDsl.Mock
        infix fun otherwiseThrows(throwable: Throwable)
    }

    interface Build<R> {
        fun toCallFake(): ((ParameterList, InvokeData) -> R)?
    }
}
