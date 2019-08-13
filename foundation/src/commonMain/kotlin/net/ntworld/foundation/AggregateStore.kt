package net.ntworld.foundation

interface AggregateStore<T : Aggregate> {
    fun save(data: T): Boolean

    fun findById(id: String): T?
}