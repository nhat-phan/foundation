package net.ntworld.foundation.cqrs

interface QueryBus {
    infix fun <R : QueryResult> process(query: Query<R>): R
}
