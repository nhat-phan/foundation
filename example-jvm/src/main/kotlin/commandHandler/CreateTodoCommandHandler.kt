package com.example.commandHandler

import com.example.contract.CreateTodoCommand
import net.ntworld.foundation.Handler
import net.ntworld.foundation.Message
import net.ntworld.foundation.cqrs.CommandHandler

@Handler
class CreateTodoCommandHandler : CommandHandler<CreateTodoCommand> {
    override fun handle(command: CreateTodoCommand) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}