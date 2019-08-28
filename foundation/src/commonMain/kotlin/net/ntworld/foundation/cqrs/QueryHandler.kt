package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.Message

interface QueryHandler<in T, out R> where T : Query<out R> {
    fun handle(query: T): R

    fun handle(query: T, message: Message?): R {
        return handle(query)
    }

    fun <T> use(infrastructure: Infrastructure, block: InfrastructureQueryHandlerContext.() -> T): T {
        return block.invoke(InfrastructureQueryHandlerContext(infrastructure))
    }
}