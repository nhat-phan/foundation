package com.generator.commandHandler

import com.generator.contract.CreateAccountCommand
import com.generator.contract.UpdateAccountCommand
import net.ntworld.foundation.Handler
import net.ntworld.foundation.Use
import net.ntworld.foundation.cqrs.CommandHandler

@Handler
class CreateAccountCommandHandler : CommandHandler<CreateAccountCommand> {
    @Use(contract = UpdateAccountCommand::class)
    override fun handle(command: CreateAccountCommand) {
        throw Exception("Not implemented")
    }
}
