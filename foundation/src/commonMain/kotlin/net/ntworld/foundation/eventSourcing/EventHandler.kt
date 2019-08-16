package net.ntworld.foundation.eventSourcing

interface EventHandler<T : Event> {
    fun handle(event: T)
}
