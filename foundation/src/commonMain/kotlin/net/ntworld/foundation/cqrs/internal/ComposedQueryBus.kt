package net.ntworld.foundation.cqrs.internal

import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryHandler
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.cqrs.ResolvableQueryBus
import net.ntworld.foundation.exception.QueryHandlerNotFoundException

internal class ComposedQueryBus(vararg bus: ResolvableQueryBus): ResolvableQueryBus {
    private val buses = bus

    @Suppress("UNCHECKED_CAST")
    override fun <R : QueryResult> process(query: Query<R>): R {
        val handler = this.resolve(query)
        if (null !== handler) {
            return handler.execute(query = query, message = null) as R
        }
        throw QueryHandlerNotFoundException(query.toString())
    }

    override fun resolve(instance: Query<*>): QueryHandler<*, *>? {
        for (bus in buses) {
            val handler = bus.resolve(instance)
            if (null !== handler) {
                return handler
            }
        }
        return null
    }

}