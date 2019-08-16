package net.ntworld.foundation.eventSourcing

interface EventBus {
    fun publish(eventData: EventData, event: Event)

    fun subscribe(type: String, listener: EventListener)

    fun unsubscribe(type: String, listener: EventListener)
}
