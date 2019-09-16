package net.ntworld.foundation.mocking

class CalledWithBuilderTest {
    class SyntaxTest {
        fun test(called: CalledWithBuilder.Start) {
            called.never()
            called exact 1
            called atLeast 1
            called.once()
            called onceMatch { true }
            called exact 2 onFirstCallMatch { true } onSecondCallMatch { false } onThirdCallMatch { false }
            called onCall 0 match { true } onSecondCallMatch { false } onThirdCallMatch { false }
            called atLeast 3 onSecondCallMatch { false }
        }
    }
}