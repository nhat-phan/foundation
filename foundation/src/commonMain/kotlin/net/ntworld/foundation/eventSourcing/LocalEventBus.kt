package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.internal.EventListenerCollection

object LocalEventBus : EventBus {
    private val channels = mutableMapOf<String, EventListenerCollection>()

    override fun publish(eventData: EventData, event: Event) {
        getChannel(eventData.type).process(event)
    }

    override fun subscribe(type: String, listener: EventListener) {
        getChannel(type).add(listener)
    }

    override fun unsubscribe(type: String, listener: EventListener) {
        getChannel(type).remove(listener)
    }

    private fun getChannel(type: String): EventListenerCollection {
        if (!this.channels.containsKey(type)) {
            this.channels[type] = EventListenerCollection()
        }
        return this.channels[type] as EventListenerCollection
    }
}
