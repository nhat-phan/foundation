package net.ntworld.foundation.fluency

interface Word {
    enum class Nothing { Default }

    enum class ReceivedEvent { Default }

    enum class ReceivedEvents { Default }

    enum class OrdinalCall(val value: Int) {
        First(0),
        Second(1),
        Third(2),
        Fourth(3),
        Fifth(4),
        Sixth(5),
        Seventh(6),
        Eighth(7),
        Ninth(8),
        Tenth(9),
    }
}