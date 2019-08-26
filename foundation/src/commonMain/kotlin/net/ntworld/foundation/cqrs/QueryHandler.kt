package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Message

interface QueryHandler<in T, out R> where T : Query<out R> {
    fun handle(query: T, message: Message?): R
}