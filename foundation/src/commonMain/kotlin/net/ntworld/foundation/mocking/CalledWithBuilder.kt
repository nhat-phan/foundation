package net.ntworld.foundation.mocking

interface CalledWithBuilder {
    interface Start : Calls {
        fun never() = exact(0)

        infix fun atLeast(count: Int): Calls

        infix fun exact(count: Int): Calls

        fun once() = exact(1)

        fun twice() = exact(2)

        fun thrice() = exact(3)

        infix fun onceMatch(block: (ParameterList) -> Boolean) {
            exact(1)
            onCall(0).match(block)
        }
    }

    interface Calls {
        infix fun onCall(n: Int): Action

        infix fun onFirstCallMatch(block: (ParameterList) -> Boolean) = onCall(0).match(block)

        infix fun onSecondCallMatch(block: (ParameterList) -> Boolean) = onCall(1).match(block)

        infix fun onThirdCallMatch(block: (ParameterList) -> Boolean) = onCall(2).match(block)
    }

    interface Action {
        infix fun match(block: (ParameterList) -> Boolean): Calls
    }

    interface Build {
        fun getCalledAtLeast(): Int

        fun getCalledCount(): Int

        fun toCalledWith(): ((ParameterList, InvokeData) -> Boolean)?
    }
}
