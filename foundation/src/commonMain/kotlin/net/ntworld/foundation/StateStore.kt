package net.ntworld.foundation

interface StateStore<T: State> {
    fun save(state: T): Boolean

    fun findById(id: String): T?
}