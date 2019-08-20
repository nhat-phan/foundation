package net.ntworld.foundation

interface Aggregate<T : State> {
    val id: String

    val state: T
}
