package com.example.commandHandler

import com.example.contract.CreateTodoCommand
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.CommandHandler
import net.ntworld.foundation.mocking.ManualMock
import net.ntworld.foundation.mocking.mock
import net.ntworld.foundation.mocking.verify

@Handler(version = 1)
class CreateTodoCommandHandlerV1 : CommandHandler<CreateTodoCommand> {
    override fun handle(command: CreateTodoCommand) {
    }
}

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