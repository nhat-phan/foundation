package net.ntworld.foundation.test

import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.fluency.Word
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.TestDsl

interface CommandBusCallFakeBuilder {
    interface Start<T: Command> : Calls<T> {
        @TestDsl.Mock
        fun alwaysDoesNothing()

        @TestDsl.Mock
        infix fun alwaysDoes(action: Word.Nothing) = alwaysDoesNothing()

        @TestDsl.Mock
        infix fun alwaysThrows(throwable: Throwable)

        @TestDsl.Mock
        infix fun alwaysRuns(fakeImpl: (T, InvokeData) -> Unit)
    }

    interface Action<T: Command> {
        @TestDsl.Mock
        fun doesNothing(): Chain<T>

        @TestDsl.Mock
        infix fun does(action: Word.Nothing) = doesNothing()

        @TestDsl.Mock
        infix fun throws(throwable: Throwable): Chain<T>

        @TestDsl.Mock
        infix fun runs(fakeImpl: (T) -> Unit): Chain<T>
    }

    interface Calls<T: Command> {
        @TestDsl.Mock
        infix fun onCall(n: Int): Action<T>

        @TestDsl.Mock
        infix fun on(nth: Word.OrdinalCall): Action<T> = onCall(nth.value)

        @TestDsl.Mock
        fun onFirstCallDoesNothing() = onCall(0).doesNothing()

        @TestDsl.Mock
        infix fun onFirstCallThrows(throwable: Throwable) = onCall(0).throws(throwable)

        @TestDsl.Mock
        fun onSecondCallDoesNothing() = onCall(1).doesNothing()

        @TestDsl.Mock
        infix fun onSecondCallThrows(throwable: Throwable) = onCall(1).throws(throwable)

        @TestDsl.Mock
        fun onThirdCallDoesNothing() = onCall(2).doesNothing()

        @TestDsl.Mock
        infix fun onThirdCallThrows(throwable: Throwable) = onCall(2).throws(throwable)
    }

    interface Chain<T: Command> : Calls<T> {
        @TestDsl.Mock
        fun otherwiseDoesNothing()

        @TestDsl.Mock
        infix fun otherwiseDoes(action: Word.Nothing) = otherwiseDoesNothing()

        @TestDsl.Mock
        infix fun otherwiseThrows(throwable: Throwable)

        @TestDsl.Mock
        infix fun otherwiseRuns(fakeImpl: (T, InvokeData) -> Unit)
    }
}