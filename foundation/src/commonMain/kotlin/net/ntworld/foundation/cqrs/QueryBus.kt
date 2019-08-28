package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Message

interface QueryBus {
    fun <R> process(query: Query<R>): R = process(query, null)

    fun <R> process(query: Query<R>, message: Message?): R
}
