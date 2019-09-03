package com.example.commandHandler

import com.example.contract.CreateTodoCommand
import net.ntworld.foundation.Handler
import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.cqrs.CommandHandler
import net.ntworld.foundation.mocking.ManualMock
import net.ntworld.foundation.mocking.mock
import net.ntworld.foundation.mocking.verify

@Handler
class CreateTodoCommandHandler(
    private val infrastructure: Infrastructure
) : CommandHandler<CreateTodoCommand> {
    override fun handle(command: CreateTodoCommand) {
    }
}

// TODO: implement better mock for handler
//class CreateTodoCommandHandlerMock() : ManualMock(), CommandHandler<CreateTodoCommand> {
//    override fun handle(command: CreateTodoCommand) {
//        return mockFunction(this::handle, command)
//    }
//}
//
//fun mainx() {
//    val handler = CreateTodoCommandHandlerMock()
//    verify(handler) {}
//}