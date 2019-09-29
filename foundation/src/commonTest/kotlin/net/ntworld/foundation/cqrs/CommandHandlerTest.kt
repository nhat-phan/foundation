package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.Message
import net.ntworld.foundation.mocking.ManualMock
import net.ntworld.foundation.mocking.mock
import net.ntworld.foundation.mocking.verify
import kotlin.test.Test

class CommandHandlerTest {
    class DummyCommand : Command {}

    @Test
    fun `test syntax use always returns Unit`() {
        class DummyCommandHandler(private val infrastructure: Infrastructure) : CommandHandler<DummyCommand> {
            override fun handle(command: DummyCommand) = use(infrastructure) {
                "can return anything, use() always returns Unit"
            }
        }
    }

    @Test
    fun `test execute calls handle without message params`() {
        class DummyCommandHandler : ManualMock(), CommandHandler<DummyCommand> {
            override fun execute(command: Command, message: Message?) {
                return this.mockFunction(DummyCommandHandler::execute, command, message)
            }

            override fun handle(command: DummyCommand) {
                return this.mockFunction(DummyCommandHandler::handle, command)
            }
        }

        val instance = DummyCommandHandler()
        mock(instance) {
            instance::execute callFake { _, _ -> }
            instance::handle callFake { _, _ -> }
        }
        val command = DummyCommand()
        instance.execute(command, null)

        verify(instance) {
            instance::handle calledWith { params ->
                val (passedCommand) = params

                1 == params.size && passedCommand === command
            }
        }
    }
}