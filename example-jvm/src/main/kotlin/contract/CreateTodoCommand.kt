package com.example.contract

import net.ntworld.foundation.Messaging
import net.ntworld.foundation.cqrs.Command

@Messaging(channel = "todo")
interface CreateTodoCommand : Command {
    val task: String
}
