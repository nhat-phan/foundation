package net.ntworld.foundation

import kotlin.reflect.KClass

open class InfrastructureProvider(
    next: Infrastructure? = null
) : Infrastructure {
    private var included: List<InfrastructureProvider> = listOf()
    private var next: Infrastructure? = null
    private var nextOrigin: Infrastructure? = next
    var root: InfrastructureProvider = this
        private set

    init {
        wire()
    }

    protected fun wire(root: InfrastructureProvider, list: List<InfrastructureProvider>) {
        this.root = root
        this.included = list
        wire()
    }

    private fun wire() {
        if (included.isEmpty()) {
            next = nextOrigin
            return
        }

        var accumulator: InfrastructureProvider = this
        for (index in 0..included.lastIndex) {
            included[index].root = this.root

            accumulator.next = included[index]
            accumulator = included[index]
        }
    }

    override fun setNext(next: Infrastructure): Infrastructure {
        if (included.isEmpty()) {
            this.next = next

            return next
        }
        return included.last().setNext(next)
    }

    override fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A> {
        if (null !== next) {
            return next!!.factoryOf(type)
        }
        throw CannotResolveException("Infrastructure.factoryOf() cannot resolve $type")
    }

    override fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A> {
        if (null !== next) {
            return next!!.storeOf(type)
        }
        throw CannotResolveException("Infrastructure.storeOf() cannot resolve $type")
    }

    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator {
        if (null !== next) {
            return next!!.idGeneratorOf(type)
        }
        throw CannotResolveException("Infrastructure.idGeneratorOf() cannot resolve $type")
    }

}