package net.ntworld.foundation

interface Aggregate<T : State> : Entity {
    val isGenerated: Boolean
    val data: T

    val isExists: Boolean
        get() = !isGenerated
}
