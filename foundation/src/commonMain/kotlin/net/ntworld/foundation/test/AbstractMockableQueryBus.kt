package net.ntworld.foundation.test

import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.cqrs.QueryHandler
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import kotlin.reflect.KClass

abstract class AbstractMockableQueryBus<T>(
    private val bus: T
) : MockableBus(), QueryBus, LocalBusResolver<Query<*>, QueryHandler<*, *>>
    where T : QueryBus, T : LocalBusResolver<Query<*>, QueryHandler<*, *>> {

    abstract fun guessQueryKClassByInstance(instance: Query<*>): KClass<out Query<*>>?

    @Suppress("UNCHECKED_CAST")
    override fun <R : QueryResult> process(query: Query<R>): R {
        val kClass = guessQueryKClassByInstance(query) ?: query::class
        val mock = handlerMocks[kClass] as HandlerManualMock<Query<R>, R>?
        if (null === mock) {
            return bus.process(query)
        }

        val realHandler = resolve(query) as QueryHandler<Query<R>, R>?
        if (null !== realHandler) {
            mock.setHandleFallbackIfNotMocked { realHandler.handle(query) }
        }
        return mock.handle(query)
    }

    override fun resolve(instance: Query<*>) = bus.resolve(instance)

    @Suppress("UNCHECKED_CAST")
    infix fun <R : QueryResult> whenProcessing(query: KClass<out Query<R>>): CallFakeBuilder.Start<R> {
        return (initMockInstanceForHandlerIfNeeded<Query<R>, R>(query) as HandlerManualMock<Query<R>, R>).whenHandleCalled()
    }

    infix fun <R : QueryResult> shouldProcess(query: KClass<out Query<R>>): CalledWithBuilder.Start {
        return initMockInstanceForHandlerIfNeeded<Query<R>, R>(query).expectHandleCalled()
    }
}