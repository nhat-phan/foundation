package net.ntworld.foundation

interface Aggregate: Entity {
    val isGenerated: Boolean

    val isExists: Boolean
        get() = !isGenerated
}
