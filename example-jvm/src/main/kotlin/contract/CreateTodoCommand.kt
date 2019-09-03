package com.example.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Messaging
import net.ntworld.foundation.cqrs.Command

@Messaging(channel = "todo")
interface CreateTodoCommand : CommonCommand {
    @Faked(type = FakedData.StarTrek.specie)
    val task: List<String>

    @get:Faked(type = FakedData.StarTrek.location)
    val zebra: String

    @get:EmailFaked
    val id: String

    override val test: String

    val something: String
}

interface CommonCommand: Command {
    @Faked(type = FakedData.StarTrek.villain)
    val test: String
}

annotation class EmailFaked
