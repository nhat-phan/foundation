package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Message

interface QueryBus {
    fun <Q : Query<R>, R> process(query: Q): R = process(query, null)

    fun <Q : Query<R>, R> process(query: Q, message: Message?): R
}
