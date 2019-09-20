package net.ntworld.foundation.mocking

import net.ntworld.foundation.fluency.Word

interface CalledWithBuilder {
    interface Start : Calls {
        @TestDsl.Verify
        fun never() = exact(0)

        @TestDsl.Verify
        infix fun atLeast(count: Int): Calls

        @TestDsl.Verify
        infix fun exact(count: Int): Calls

        @TestDsl.Verify
        infix fun alwaysMatch(verify: (ParameterList, InvokeData) -> Boolean)

        @TestDsl.Verify
        fun once() = exact(1)

        @TestDsl.Verify
        fun twice() = exact(2)

        @TestDsl.Verify
        fun thrice() = exact(3)

        @TestDsl.Verify
        infix fun onceMatch(verify: (ParameterList) -> Boolean) {
            exact(1)
            onCall(0).match(verify)
        }
    }

    interface Calls {
        @TestDsl.Verify
        infix fun onCall(n: Int): Action

        @TestDsl.Verify
        infix fun on(nth: Word.OrdinalCall): Action = onCall(nth.value)

        @TestDsl.Verify
        infix fun onFirstCallMatch(verify: (ParameterList) -> Boolean) = onCall(0).match(verify)

        @TestDsl.Verify
        infix fun onSecondCallMatch(verify: (ParameterList) -> Boolean) = onCall(1).match(verify)

        @TestDsl.Verify
        infix fun onThirdCallMatch(verify: (ParameterList) -> Boolean) = onCall(2).match(verify)
    }

    interface Action {
        @TestDsl.Verify
        infix fun match(verify: (ParameterList) -> Boolean): Chain
    }

    interface Chain : Calls {
        @TestDsl.Verify
        infix fun otherwiseMatch(verify: (ParameterList, InvokeData) -> Boolean)
    }

    interface Build {
        fun getCalledAtLeast(): Int

        fun getCalledCount(): Int

        fun toCalledWith(): ((ParameterList, InvokeData) -> Boolean)?
    }
}
