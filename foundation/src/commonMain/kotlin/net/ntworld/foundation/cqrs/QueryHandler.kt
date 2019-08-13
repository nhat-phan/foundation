package net.ntworld.foundation.cqrs

interface QueryHandler<in T, out R> where T : Query<out R> {
    fun handle(query: T): R
}