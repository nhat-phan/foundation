package net.ntworld.foundation.cqrs

interface QueryBus {
    fun <R: QueryResult> process(query: Query<R>): R
}
