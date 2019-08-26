package com.test.event.kotlin

import net.ntworld.foundation.Event
import net.ntworld.foundation.eventSourcing.EventSourcing

// @Messaging
@EventSourcing("created")
interface UpdatedEvent: Event {
    val id: String

    val companyId: String

    val email: String

    val name: String

    val time: String
}