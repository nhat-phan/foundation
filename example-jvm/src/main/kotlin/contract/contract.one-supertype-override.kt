package com.example.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandHandler

interface OneSupertypeOverrideContractParent {
    @get:Faked(type = FakedData.Zelda.character)
    val zelda: String

    @Faked(type = FakedData.Name.firstName)
    val firstName: String

    @Faked(type = FakedData.Name.lastName)
    val lastName: String
}

interface OneSupertypeOverrideContract : OneSupertypeOverrideContractParent, Command {
    @EmailFaked
    val email: String

    override val firstName: String

    override val lastName: String

    companion object
}

@Handler
class OneSupertypeOverrideContractHandler : CommandHandler<OneSupertypeOverrideContract> {
    override fun handle(command: OneSupertypeOverrideContract) {
    }
}
