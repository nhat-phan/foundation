package net.ntworld.foundation.cqrs

interface CommandHandler<T: Command> {
    fun handle(command: T)
}