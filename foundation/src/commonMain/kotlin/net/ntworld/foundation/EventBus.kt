package net.ntworld.foundation

interface EventBus {
    fun publish(event: Event)

    fun process(event: Event)
}
