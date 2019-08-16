package net.ntworld.foundation.cqrs

interface QueryBus {
    fun <Q : Query<R>, R> process(query: Q): R

    fun <Q : Query<R>, R> process(query: Q, block: (R) -> Unit)
}
