package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.Message

interface CommandHandler<T : Command> {
    fun handle(command: T)

    fun handle(command: T, message: Message?) {
        handle(command)
    }

    fun <T> use(infrastructure: Infrastructure, block: InfrastructureCommandHandlerContext.() -> T): T {
        return block.invoke(InfrastructureCommandHandlerContext(infrastructure))
    }
}