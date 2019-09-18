package net.ntworld.foundation.test

import net.ntworld.foundation.mocking.CalledWithBuilder

interface PublishCallFakeBuilder {
    interface Start<R> : Calls<R> {
        fun alwaysPublishes()

        fun alwaysDoNothing()
    }

    interface Action<R> {
        fun publishes(): Chain<R>

        fun doesNothing(): Chain<R>
    }

    interface Calls<R> {
        infix fun onCall(n: Int): Action<R>

        fun onFirstCallDoesNothing() = onCall(0).doesNothing()

        fun onSecondCallDoesNothing() = onCall(1).doesNothing()

        fun onThirdCallDoesNothing() = onCall(2).doesNothing()
    }

    interface Chain<R> : Calls<R> {
        fun otherwisePublishes()

        fun otherwiseDoesNothing()
    }

    interface Build : CalledWithBuilder.Build
}