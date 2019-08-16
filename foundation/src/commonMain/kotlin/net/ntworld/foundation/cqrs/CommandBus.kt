package net.ntworld.foundation.cqrs

interface CommandBus {
    fun process(command: Command)
}