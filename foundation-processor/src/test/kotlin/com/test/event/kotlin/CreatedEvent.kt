package com.test.event.kotlin

import net.ntworld.foundation.FakedData
import net.ntworld.foundation.eventSourcing.Event
import net.ntworld.foundation.eventSourcing.EventSourcing
import net.ntworld.foundation.eventSourcing.EventSourcing.Encrypted
import net.ntworld.foundation.eventSourcing.EventSourcing.Metadata

@EventSourcing("created")
data class CreatedEvent(
    val id: String,

    @Metadata
    val companyId: String,

    @Encrypted
    val email: String,

    @Encrypted(FakedData.Name.fullName)
    val name: String,

    @Metadata
    val time: String
) : Event