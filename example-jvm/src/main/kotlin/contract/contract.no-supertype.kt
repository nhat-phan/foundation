package com.example.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandHandler


interface NoSupertypeContractCommand : Command {
    @get:Faked(type = FakedData.Zelda.character)
    val zelda: String

    @Faked(type = FakedData.StarTrek.character)
    val name: String

    @EmailFaked
    val email: String?

    @NoAffectedFaked
    val list: List<String>

    val phones: List<Int>

    companion object
}

@Handler
class NoSupertypeContractCommandHandler : CommandHandler<NoSupertypeContractCommand> {
    override fun handle(command: NoSupertypeContractCommand) {
    }
}

