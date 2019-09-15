package net.ntworld.foundation.mocking

import kotlin.test.Test

class CallFakeBuilderTest {
    class SyntaxTest {
        fun test(builder: CallFakeBuilder.Start<Int>) {
            builder alwaysReturns 1000
            builder onCall 0 returns 0 otherwiseReturns -1
            builder onFirstCallReturns 1 onCall 2 returns 2 otherwiseReturns 100
            builder onFirstCallReturns 1 onSecondCallReturns 2 otherwiseReturns -1
        }
    }
}