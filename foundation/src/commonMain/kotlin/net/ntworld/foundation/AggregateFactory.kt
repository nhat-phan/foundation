package net.ntworld.foundation

interface AggregateFactory<out A: Aggregate> {
    fun generate(): A

    fun retrieve(id: String): A?

    // fun retrieveAll(): Iterable<A>

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
            throw Exception("Not found")
        }
        return instance
    }
}