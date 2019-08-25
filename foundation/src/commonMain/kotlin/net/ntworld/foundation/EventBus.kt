package net.ntworld.foundation

import net.ntworld.foundation.eventSourcing.EventEntity

interface EventBus {
    fun publish(event: Event)

}
