package net.ntworld.foundation.test

import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.cqrs.QueryHandler
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.TestDsl
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.mocking.internal.CalledWithBuilderImpl
import net.ntworld.foundation.test.internal.BusCalledWithBuilderImpl
import net.ntworld.foundation.test.internal.QueryBusCallFakeBuilderImpl
import kotlin.reflect.KClass

abstract class AbstractMockableQueryBus<T>(
    private val bus: T
) : MockableBus(), QueryBus, LocalBusResolver<Query<*>, QueryHandler<*, *>>
    where T : QueryBus, T : LocalBusResolver<Query<*>, QueryHandler<*, *>> {

    abstract fun guessQueryKClassByInstance(instance: Query<*>): KClass<out Query<*>>?

    val originalBus: QueryBus = bus
    val originalProcess: (Query<*>, InvokeData) -> Unit = { query, _ ->
        bus.process(query)
    }

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
    @TestDsl.Mock
    infix fun <Q : Query<R>, R : QueryResult> whenProcessing(query: KClass<out Q>): QueryBusCallFakeBuilder.Start<Q, R> {
        val start = (initMockInstanceForHandlerIfNeeded<Query<R>, R>(query) as HandlerManualMock<Query<R>, R>)
            .whenHandleCalled()

        return QueryBusCallFakeBuilderImpl(start as CallFakeBuilderImpl<R>)
    }

    @TestDsl.Verify
    infix fun <T : Query<R>, R : QueryResult> shouldProcess(query: KClass<out T>): BusCalledWithBuilder.Start<T> {
        val start = initMockInstanceForHandlerIfNeeded<Query<R>, R>(query).expectHandleCalled()

        return BusCalledWithBuilderImpl(start as CalledWithBuilderImpl)
    }
}