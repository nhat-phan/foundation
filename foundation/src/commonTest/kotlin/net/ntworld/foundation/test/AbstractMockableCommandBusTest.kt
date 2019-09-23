package net.ntworld.foundation.test

import net.ntworld.foundation.HandlerVersioningStrategy
import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.CommandHandler
import net.ntworld.foundation.fluency.nothing
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.TestDsl
import kotlin.reflect.KClass
import kotlin.test.*

class AbstractMockableCommandBusTest {
    private interface CreateCommand : Command {
        val name: String

        companion object
    }

    private class CreateCommandImpl(override val name: String) : CreateCommand

    private interface UpdateCommand : Command {
        val email: String

        companion object
    }

    private class UpdateCommandImpl(override val email: String) : UpdateCommand

    private data class TestData(
        var createCommandRan: Boolean = false,
        var updateCommandRan: Boolean = false
    )

    private class CreateCommandHandler(private val data: TestData) : CommandHandler<CreateCommand> {
        override fun handle(command: CreateCommand) {
            data.createCommandRan = true
        }
    }

    private class UpdateCommandHandler(private val data: TestData) : CommandHandler<UpdateCommand> {
        override fun handle(command: UpdateCommand) {
            data.updateCommandRan = true
        }
    }

    private class LocalCommandBus(private val data: TestData) : CommandBus,
        LocalBusResolver<Command, CommandHandler<*>> {
        override fun process(command: Command) {
            val handler = this.resolve(command)
            if (null !== handler) {
                handler.execute(command = command, message = null)
            }
        }

        fun getVersioningStrategy(command: Command): HandlerVersioningStrategy =
            HandlerVersioningStrategy.useLatestVersion

        override fun resolve(instance: Command): CommandHandler<*>? {
            val strategy = getVersioningStrategy(instance)
            if (strategy.skip()) {
                return null
            }

            return when (instance) {
                is CreateCommand -> CreateCommandHandler(data)
                is UpdateCommand -> UpdateCommandHandler(data)
                else -> null
            }
        }
    }

    private class MockableCommandBus<T>(private val bus: T) : AbstractMockableCommandBus<T>(bus)
        where T : CommandBus, T : LocalBusResolver<Command, CommandHandler<*>> {

        override fun guessCommandKClassByInstance(instance: Command): KClass<out Command>? {
            return when (instance) {
                is CreateCommand -> CreateCommand::class
                is UpdateCommand -> UpdateCommand::class
                else -> null
            }
        }

        @TestDsl.Mock
        infix fun whenProcessing(command: CreateCommand.Companion): CommandBusCallFakeBuilder.Start<CreateCommand> {
            return whenProcessing(CreateCommand::class)
        }

        @TestDsl.Mock
        infix fun whenProcessing(command: UpdateCommand.Companion): CommandBusCallFakeBuilder.Start<UpdateCommand> {
            return whenProcessing(UpdateCommand::class)
        }

        @TestDsl.Verify
        infix fun shouldProcess(request: CreateCommand.Companion): CalledWithBuilder.Start {
            return shouldProcess(CreateCommand::class)
        }

        @TestDsl.Verify
        infix fun shouldProcess(request: UpdateCommand.Companion): CalledWithBuilder.Start {
            return shouldProcess(UpdateCommand::class)
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    private fun makeMockableCommandBus(data: TestData): MockableCommandBus<LocalCommandBus> {
        return MockableCommandBus(LocalCommandBus(data))
    }

    @Test
    fun `test whenProcessing alwaysDoes nothing`() {
        val data = TestData()
        val bus = makeMockableCommandBus(data)

        bus whenProcessing CreateCommand alwaysDoes nothing

        bus.process(CreateCommandImpl(name = "create"))
        bus.process(UpdateCommandImpl(email = "update"))

        assertFalse(data.createCommandRan)
        assertTrue(data.updateCommandRan)
    }

    @Test
    fun `test whenProcessing alwaysThrows exception`() {
        val data = TestData()
        val bus = makeMockableCommandBus(data)
        val exception = Exception()

        bus whenProcessing CreateCommand alwaysThrows exception

        try {
            bus.process(CreateCommandImpl(name = "create"))
        } catch (thrown: Throwable) {
            assertSame(exception, thrown)
        }
        bus.process(UpdateCommandImpl(email = "update"))

        assertFalse(data.createCommandRan)
        assertTrue(data.updateCommandRan)
    }

    @Test
    fun `test whenProcessing alwaysRuns`() {
        val data = TestData()
        val bus = makeMockableCommandBus(data)

        bus whenProcessing CreateCommand alwaysRuns bus.originalProcess

        bus.process(CreateCommandImpl(name = "create"))
        bus.process(UpdateCommandImpl(email = "update"))

        assertTrue(data.createCommandRan)
        assertTrue(data.updateCommandRan)
    }

    @Test
    fun `test shouldProcess exact`() {
        val data = TestData()
        val bus = makeMockableCommandBus(data)

        bus shouldProcess CreateCommand exact 2
        bus shouldProcess UpdateCommand exact 0

        bus.process(CreateCommandImpl(name = "create 1"))
        bus.process(CreateCommandImpl(name = "create 2"))

        assertTrue(data.createCommandRan)
        assertFalse(data.updateCommandRan)
        bus.verifyAll()
    }

    @Test
    fun `test shouldProcess atLeast`() {
        val data = TestData()
        val bus = makeMockableCommandBus(data)

        bus shouldProcess CreateCommand atLeast 1
        bus shouldProcess UpdateCommand atLeast 0

        bus.process(CreateCommandImpl(name = "create 1"))
        bus.process(CreateCommandImpl(name = "create 2"))

        assertTrue(data.createCommandRan)
        assertFalse(data.updateCommandRan)
        bus.verifyAll()
    }

    @Test
    fun `test shouldProcess onFirstCallMatch`() {
        val data = TestData()
        val bus = makeMockableCommandBus(data)

        bus shouldProcess CreateCommand onFirstCallMatch { params ->
            val (command) = params
            (command as CreateCommand).name == "create 1"
        } otherwiseMatch { _, _ -> true }

        bus.process(CreateCommandImpl(name = "create 1"))
        bus.process(CreateCommandImpl(name = "create 2"))

        assertTrue(data.createCommandRan)
        assertFalse(data.updateCommandRan)
        bus.verifyAll()
    }
}