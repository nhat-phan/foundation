package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.Message

interface CommandHandler<T : Command> {
    fun handle(command: T)

    @Suppress("UNCHECKED_CAST")
    fun execute(command: Command, message: Message?) = handle(command as T)

    fun <T> use(infrastructure: Infrastructure, block: InfrastructureCommandHandlerContext.() -> T): T {
        return block.invoke(InfrastructureCommandHandlerContext(infrastructure))
    }
}