package net.ntworld.foundation.eventSourcing

interface EventBus {
    fun publish(event: Event)

    fun subscribe(type: String)

    fun unsubscribe(type: String)
}
