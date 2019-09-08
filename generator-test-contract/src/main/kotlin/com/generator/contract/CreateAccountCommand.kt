package com.generator.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.cqrs.Command

interface CreateAccountCommand : Command {
    val jobPositionId: String

    @get:Faked(type = FakedData.Internet.emailAddress)
    val email: String

    @get:FakedFirstName
    val firstName: String

    @get:Faked(type = FakedData.Name.lastName)
    val parentFaked: String

    companion object
}