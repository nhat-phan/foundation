package net.ntworld.foundation

interface EventHandler<T : Event> {
    fun handle(event: T, message: Message?)
}
