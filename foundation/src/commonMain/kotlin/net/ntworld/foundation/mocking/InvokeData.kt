package net.ntworld.foundation.mocking

data class InvokeData(
    val callIndex: Int
) {
    val ordinal: Int = callIndex + 1
}