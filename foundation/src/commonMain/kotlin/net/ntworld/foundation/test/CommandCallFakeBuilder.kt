package net.ntworld.foundation.test

import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.ParameterList

interface CommandCallFakeBuilder {
    interface Start : Calls {
        fun alwaysDoesNothing()

        infix fun alwaysDoes(action: FluentWord.Nothing) = alwaysDoesNothing()

        infix fun alwaysThrows(throwable: Throwable)

        infix fun run(fakeImpl: (ParameterList, InvokeData) -> Unit)
    }

    interface Action {
        fun doesNothing(): Chain

        infix fun does(action: FluentWord.Nothing) = doesNothing()

        infix fun throws(throwable: Throwable): Chain
    }

    interface Calls {
        infix fun onCall(n: Int): Action

        fun onFirstCallDoesNothing() = onCall(0).doesNothing()

        infix fun onFirstCallThrows(throwable: Throwable) = onCall(0).throws(throwable)

        fun onSecondCallDoesNothing() = onCall(1).doesNothing()

        infix fun onSecondCallThrows(throwable: Throwable) = onCall(1).throws(throwable)

        fun onThirdCallDoesNothing() = onCall(2).doesNothing()

        infix fun onThirdCallThrows(throwable: Throwable) = onCall(2).throws(throwable)
    }

    interface Chain : Calls {
        fun otherwiseDoesNothing()

        infix fun otherwiseDoes(action: FluentWord.Nothing) = otherwiseDoesNothing()

        infix fun otherwiseThrows(throwable: Throwable)
    }
}