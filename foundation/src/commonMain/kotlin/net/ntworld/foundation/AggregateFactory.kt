package net.ntworld.foundation

import net.ntworld.foundation.exception.NotFoundException

interface AggregateFactory<out A : Aggregate<S>, S : State> {
    fun make(state: S): A

    fun generate(): A

    fun retrieveOrNull(id: String): A?

    fun retrieve(id: String): A {
        val instance = this.retrieveOrNull(id)
        if (null === instance) {
            throw NotFoundException()
        }
        return instance
    }

    fun retrieveOrFail(id: String): A = retrieve(id)

    fun retrieveOrGenerate(id: String): A {
        val instance = this.retrieveOrNull(id)
        if (null === instance) {
            return this.generate()
        }
        return instance
    }

}