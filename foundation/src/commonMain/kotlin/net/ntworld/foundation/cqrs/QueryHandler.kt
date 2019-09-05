package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.Message

interface QueryHandler<in T, out R: QueryResult> where T : Query<out R> {
    fun handle(query: T): R

    @Suppress("UNCHECKED_CAST")
    fun execute(query: Query<*>, message: Message?): R = handle(query as T)

    fun <T> use(infrastructure: Infrastructure, block: InfrastructureQueryHandlerContext.() -> T): T {
        return block.invoke(InfrastructureQueryHandlerContext(infrastructure))
    }
}