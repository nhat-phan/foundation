package net.ntworld.foundation.mocking

interface CalledWithFactory {
    fun shouldNeverCalled()

    fun shouldCalledAtLeast(n: Int)

    fun shouldCalledExact(n: Int)

    fun shouldMatch()

    fun onCall(n: Int)

    fun onFirstCall() = onCall(0)

    fun onSecondCall() = onCall(1)

    fun onThirdCall() = onCall(2)
}
