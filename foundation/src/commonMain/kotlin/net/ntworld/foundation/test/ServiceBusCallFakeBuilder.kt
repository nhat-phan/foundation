package net.ntworld.foundation.test

import net.ntworld.foundation.Request
import net.ntworld.foundation.Response
import net.ntworld.foundation.fluency.Word
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.TestDsl

interface ServiceBusCallFakeBuilder {
    interface Start<T : Request<R>, R : Response> : Calls<T, R> {
        @TestDsl.Mock
        infix fun alwaysReturns(result: R)

        @TestDsl.Mock
        infix fun alwaysThrows(throwable: Throwable)

        @TestDsl.Mock
        infix fun alwaysRuns(fakeImpl: (T, InvokeData) -> R)
    }

    interface Action<T : Request<R>, R : Response> {
        @TestDsl.Mock
        infix fun returns(result: R): Chain<T, R>

        @TestDsl.Mock
        infix fun throws(throwable: Throwable): Chain<T, R>

        @TestDsl.Mock
        infix fun runs(fakeImpl: (T) -> R): Chain<T, R>
    }

    interface Calls<T : Request<R>, R : Response> {
        @TestDsl.Mock
        infix fun onCall(n: Int): Action<T, R>

        @TestDsl.Mock
        infix fun on(nth: Word.OrdinalCall): Action<T, R> = onCall(nth.value)

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

    interface Chain<T : Request<R>, R : Response> : Calls<T, R> {
        @TestDsl.Mock
        infix fun otherwiseReturns(result: R)

        @TestDsl.Mock
        infix fun otherwiseThrows(throwable: Throwable)

        @TestDsl.Mock
        infix fun otherwiseRuns(fakeImpl: (T, InvokeData) -> R)
    }

}