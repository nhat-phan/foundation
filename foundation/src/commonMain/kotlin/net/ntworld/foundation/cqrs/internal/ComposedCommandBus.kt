package net.ntworld.foundation.cqrs.internal

import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandHandler
import net.ntworld.foundation.cqrs.ResolvableCommandBus

internal class ComposedCommandBus(vararg bus: ResolvableCommandBus): ResolvableCommandBus {
    private val buses = bus

    override fun process(command: Command) {
        val handler = this.resolve(command)
        if (null !== handler) {
            handler.execute(command = command, message = null)
        }
    }

    override fun resolve(instance: Command): CommandHandler<*>? {
        for (bus in buses) {
            val handler = bus.resolve(instance)
            if (null !== handler) {
                return handler
            }
        }
        return null
    }

}