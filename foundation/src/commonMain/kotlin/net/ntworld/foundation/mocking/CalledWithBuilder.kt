package net.ntworld.foundation.mocking

interface CalledWithBuilder {
    interface Start : Calls {
        fun never() = exact(0)

        infix fun atLeast(count: Int): Calls

        infix fun exact(count: Int): Calls

        infix fun alwaysMatch(verify: (ParameterList, InvokeData) -> Boolean)

        fun once() = exact(1)

        fun twice() = exact(2)

        fun thrice() = exact(3)

        infix fun onceMatch(verify: (ParameterList) -> Boolean) {
            exact(1)
            onCall(0).match(verify)
        }
    }

    interface Calls {
        infix fun onCall(n: Int): Action

        infix fun onFirstCallMatch(verify: (ParameterList) -> Boolean) = onCall(0).match(verify)

        infix fun onSecondCallMatch(verify: (ParameterList) -> Boolean) = onCall(1).match(verify)

        infix fun onThirdCallMatch(verify: (ParameterList) -> Boolean) = onCall(2).match(verify)
    }

    interface Action {
        infix fun match(verify: (ParameterList) -> Boolean): Chain
    }

    interface Chain : Calls {
        infix fun otherwiseMatch(verify: (ParameterList, InvokeData) -> Boolean)
    }

    interface Build {
        fun getCalledAtLeast(): Int

        fun getCalledCount(): Int

        fun toCalledWith(): ((ParameterList, InvokeData) -> Boolean)?
    }
}
