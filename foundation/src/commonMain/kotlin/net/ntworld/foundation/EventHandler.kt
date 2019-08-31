package net.ntworld.foundation

interface EventHandler<T : Event> {
    fun handle(event: T)

    @Suppress("UNCHECKED_CAST")
    fun execute(event: Event, message: Message?) = handle(event as T)
}
