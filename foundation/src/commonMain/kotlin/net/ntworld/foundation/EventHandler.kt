package net.ntworld.foundation

interface EventHandler<T : Event> {
    fun handle(event: T)

    fun execute(event: T, message: Message?) = handle(event)
}
