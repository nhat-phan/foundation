package com.example.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Handler
import net.ntworld.foundation.Messaging
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandHandler

@Messaging(channel = "todo")
interface CreateTodoCommand : Command {
    @Faked(type = FakedData.StarTrek.specie)
    val task: List<String>

    @get:Faked(type = FakedData.StarTrek.location)
    val zebra: String

    val id: String

    val something: String
}
