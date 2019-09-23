package net.ntworld.foundation.test

import net.ntworld.foundation.fluency.Word
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.TestDsl

interface BusCalledWithBuilder {
    interface Start<T> : Calls<T> {
        @TestDsl.Verify
        fun never() = exact(0)

        @TestDsl.Verify
        infix fun never(called: Word.Called) = exact(0)

        @TestDsl.Verify
        infix fun atLeast(count: Int): Calls<T>

        @TestDsl.Verify
        infix fun exact(count: Int): Calls<T>

        @TestDsl.Verify
        infix fun exact(count: Word.CalledCount): Calls<T> = exact(count.value)

        @TestDsl.Verify
        infix fun alwaysMatch(verify: (T, InvokeData) -> Boolean)

        @TestDsl.Verify
        fun once() = exact(1)

        @TestDsl.Verify
        fun twice() = exact(2)

        @TestDsl.Verify
        fun thrice() = exact(3)

        @TestDsl.Verify
        infix fun onceMatch(verify: (T) -> Boolean) {
            exact(1)
            onCall(0).match(verify)
        }
    }

    interface Calls<T> {
        @TestDsl.Verify
        infix fun onCall(n: Int): Action<T>

        @TestDsl.Verify
        infix fun on(nth: Word.OrdinalCall): Action<T> = onCall(nth.value)

        @TestDsl.Verify
        infix fun onFirstCallMatch(verify: (T) -> Boolean) = onCall(0).match(verify)

        @TestDsl.Verify
        infix fun onSecondCallMatch(verify: (T) -> Boolean) = onCall(1).match(verify)

        @TestDsl.Verify
        infix fun onThirdCallMatch(verify: (T) -> Boolean) = onCall(2).match(verify)
    }

    interface Action<T> {
        @TestDsl.Verify
        infix fun match(verify: (T) -> Boolean): Chain<T>
    }

    interface Chain<T> : Calls<T> {
        @TestDsl.Verify
        infix fun otherwiseMatch(verify: (T, InvokeData) -> Boolean)
    }
}