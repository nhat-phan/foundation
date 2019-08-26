package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Message

interface CommandHandler<T: Command> {
    fun handle(command: T, message: Message?)
}