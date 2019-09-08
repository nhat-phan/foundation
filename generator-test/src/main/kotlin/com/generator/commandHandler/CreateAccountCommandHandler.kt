package com.generator.commandHandler

import com.generator.contract.CreateAccountCommand
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.CommandHandler

@Handler
class CreateAccountCommandHandler : CommandHandler<CreateAccountCommand> {
    override fun handle(command: CreateAccountCommand) {
        throw Exception("Not implemented")
    }
}
