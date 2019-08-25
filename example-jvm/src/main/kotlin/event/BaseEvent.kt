package com.example.event

import net.ntworld.foundation.eventSourcing.EventSourcing

@EventSourcing("event", 0)
interface BaseEvent {
    // @EventSourcing.Encrypted
    val id: String
}