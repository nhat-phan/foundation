package net.ntworld.foundation.test

import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.CommandHandler
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.TestDsl
import net.ntworld.foundation.mocking.internal.CallFakeBuilderImpl
import net.ntworld.foundation.test.internal.CommandCallFakeBuilderImpl
import kotlin.reflect.KClass

abstract class AbstractMockableCommandBus<T>(
    private val bus: T
) : MockableBus(), CommandBus, LocalBusResolver<Command, CommandHandler<*>>
    where T : CommandBus, T : LocalBusResolver<Command, CommandHandler<*>> {

    abstract fun guessCommandKClassByInstance(instance: Command): KClass<out Command>?

    @Suppress("UNCHECKED_CAST")
    override fun process(command: Command) {
        val kClass = guessCommandKClassByInstance(command) ?: command::class
        val mock = handlerMocks[kClass] as HandlerManualMock<Command, Unit>?
        if (null === mock) {
            return bus.process(command)
        }

        val realHandler = resolve(command) as CommandHandler<Command>?
        if (null !== realHandler) {
            mock.setHandleFallbackIfNotMocked { realHandler.handle(command) }
        }
        mock.handle(command)
    }

    override fun resolve(instance: Command) = bus.resolve(instance)

    @Suppress("UNCHECKED_CAST")
    @TestDsl.Mock
    infix fun whenProcessing(command: KClass<out Command>): CommandCallFakeBuilder.Start {
        val start = (initMockInstanceForHandlerIfNeeded<Command, Unit>(command) as HandlerManualMock<Command, Unit>)
            .whenHandleCalled()

        return CommandCallFakeBuilderImpl(start as CallFakeBuilderImpl<Unit>)
    }

    @TestDsl.Verify
    infix fun shouldProcess(command: KClass<out Command>): CalledWithBuilder.Start {
        return initMockInstanceForHandlerIfNeeded<Command, Unit>(command).expectHandleCalled()
    }
}