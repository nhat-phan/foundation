package com.example.event

import net.ntworld.foundation.Event
import net.ntworld.foundation.Messaging

@Messaging(channel = "todo")
interface TodoDeletedEvent : Event {
    val id: String
}
