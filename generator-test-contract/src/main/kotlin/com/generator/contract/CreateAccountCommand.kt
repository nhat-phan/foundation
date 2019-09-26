package com.generator.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Messaging
import net.ntworld.foundation.cqrs.Command

@Messaging(channel = "create-account")
interface CreateAccountCommand : Command, AccountProperties {
    companion object
}