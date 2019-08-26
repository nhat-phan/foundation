package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Message

interface CommandBus {
    fun process(command: Command) = process(command, null)

    fun process(command: Command, message: Message?)
}