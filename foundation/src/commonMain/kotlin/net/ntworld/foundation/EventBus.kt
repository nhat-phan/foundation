package net.ntworld.foundation

import net.ntworld.foundation.internal.ComposedEventBus

interface EventBus {
    infix fun publish(event: Event)

    infix fun process(event: Event)

    companion object {
        fun compose(vararg bus: ResolvableEventBus): ResolvableEventBus {
            return ComposedEventBus(*bus)
        }
    }
}
