package net.ntworld.foundation

import net.ntworld.foundation.exception.NotFoundException

interface AggregateFactory<out A : Aggregate<S>, S : State> {
    fun make(state: S): A

    fun generate(): A

    fun retrieve(id: String): A?

    fun retrieveOrGenerate(id: String): A {
        val instance = this.retrieve(id)
        if (null === instance) {
            return this.generate()
        }
        return instance
    }

    fun retrieveOrFail(id: String): A {
        val instance = this.retrieve(id)
        if (null === instance) {
            throw NotFoundException()
        }
        return instance
    }
}