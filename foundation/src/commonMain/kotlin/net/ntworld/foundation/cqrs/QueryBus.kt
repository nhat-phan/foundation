package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Message

interface QueryBus {
    fun <R> process(query: Query<R>): R
}
