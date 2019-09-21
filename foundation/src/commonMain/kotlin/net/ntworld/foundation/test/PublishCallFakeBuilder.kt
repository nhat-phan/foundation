package net.ntworld.foundation.test

import net.ntworld.foundation.fluency.Word

interface PublishCallFakeBuilder {
    interface Start : Calls {
        fun alwaysPublishesEvents()

        infix fun alwaysPublishes(word: Word.ReceivedEvents) = alwaysPublishesEvents()

        fun alwaysDoesNothing()

        infix fun alwaysDoes(word: Word.Nothing) = alwaysDoesNothing()
    }

    interface Action {
        fun publishesReceivedEvent(): Chain

        fun publishes(word: Word.ReceivedEvent): Chain

        fun doesNothing(): Chain

        fun does(word: Word.Nothing): Chain = doesNothing()
    }

    interface Calls {
        infix fun onCall(n: Int): Action

        infix fun on(nth: Word.OrdinalCall): Action = onCall(nth.value)

        fun onFirstCallDoesNothing() = onCall(0).doesNothing()

        fun onSecondCallDoesNothing() = onCall(1).doesNothing()

        fun onThirdCallDoesNothing() = onCall(2).doesNothing()
    }

    interface Chain : Calls {
        fun otherwisePublishesEvents()

        infix fun otherwisePublishes(word: Word.ReceivedEvents) = otherwisePublishesEvents()

        fun otherwiseDoesNothing()

        infix fun otherwiseDoes(word: Word.Nothing) = otherwiseDoesNothing()
    }
}