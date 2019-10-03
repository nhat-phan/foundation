package net.ntworld.foundation.cqrs

import net.ntworld.foundation.cqrs.internal.ComposedQueryBus

interface QueryBus {
    infix fun <R : QueryResult> process(query: Query<R>): R

    companion object {
        fun composed(vararg bus: ResolvableQueryBus): ResolvableQueryBus {
            return ComposedQueryBus(*bus)
        }
    }
}
