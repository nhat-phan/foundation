package net.ntworld.foundation.cqrs

import net.ntworld.foundation.cqrs.internal.ComposedCommandBus

interface CommandBus {
    infix fun process(command: Command)

    companion object {
        fun compose(vararg bus: ResolvableCommandBus): ResolvableCommandBus {
            return ComposedCommandBus(*bus)
        }
    }
}