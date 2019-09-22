package net.ntworld.foundation.test

import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.fluency.Word
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.TestDsl

interface QueryBusCallFakeBuilder {
    interface Start<Q : Query<R>, R : QueryResult> : Calls<Q, R> {
        @TestDsl.Mock
        infix fun alwaysReturns(result: R)

        @TestDsl.Mock
        infix fun alwaysThrows(throwable: Throwable)

        @TestDsl.Mock
        infix fun alwaysRuns(fakeImpl: (Q, InvokeData) -> R)
    }

    interface Action<Q : Query<R>, R : QueryResult> {
        @TestDsl.Mock
        infix fun returns(result: R): Chain<Q, R>

        @TestDsl.Mock
        infix fun throws(throwable: Throwable): Chain<Q, R>

        @TestDsl.Mock
        infix fun runs(fakeImpl: (Q) -> R): Chain<Q, R>
    }

    interface Calls<Q : Query<R>, R : QueryResult> {
        @TestDsl.Mock
        infix fun onCall(n: Int): Action<Q, R>

        @TestDsl.Mock
        infix fun on(nth: Word.OrdinalCall): Action<Q, R> = onCall(nth.value)

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

    interface Chain<Q : Query<R>, R : QueryResult> : Calls<Q, R> {
        @TestDsl.Mock
        infix fun otherwiseReturns(result: R)

        @TestDsl.Mock
        infix fun otherwiseThrows(throwable: Throwable)

        @TestDsl.Mock
        infix fun otherwiseRuns(fakeImpl: (Q, InvokeData) -> R)
    }

}