package net.ntworld.foundation.cqrs

interface CommandBus {
    infix fun process(command: Command)
}