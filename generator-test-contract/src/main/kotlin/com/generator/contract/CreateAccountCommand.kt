package com.generator.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.cqrs.Command

interface CreateAccountCommand : Command {
    val jobPositionId: String

    @Faked(type = FakedData.Internet.emailAddress)
    val email: String

    @FakedFirstName
    val firstName: String

    @Faked(type = FakedData.Name.lastName)
    val lastName: String

    companion object
}