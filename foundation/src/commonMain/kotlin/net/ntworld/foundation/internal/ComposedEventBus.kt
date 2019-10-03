package net.ntworld.foundation.internal

import net.ntworld.foundation.*

internal class ComposedEventBus(vararg bus: ResolvableEventBus) : ResolvableEventBus {
    private val buses = bus

    override fun publish(event: Event) {
        for (bus in buses) {
            bus.publish(event)
        }
    }

    override fun process(event: Event) {
        val handlers = this.resolve(event)
        if (null !== handlers) {
            handlers.forEach { it.execute(event = event, message = null) }
        }
    }

    override fun resolve(instance: Event): Array<EventHandler<*>>? {
        for (bus in buses) {
            val handler = bus.resolve(instance)
            if (null !== handler) {
                return handler
            }
        }
        return null
    }

}