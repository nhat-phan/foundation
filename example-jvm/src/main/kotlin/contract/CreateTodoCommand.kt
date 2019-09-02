package com.example.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Messaging
import net.ntworld.foundation.cqrs.Command

@Messaging(channel = "todo")
interface CreateTodoCommand : Command {
    @Faked(type = FakedData.StarTrek.location)
    val task: List<String>

    val zebra: String

    val id: String

    val something: String
}
