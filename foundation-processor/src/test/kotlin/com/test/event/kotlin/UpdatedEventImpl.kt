package com.test.event.kotlin

import net.ntworld.foundation.Implementation
import net.ntworld.foundation.eventSourcing.EventSourcing

@Implementation
data class UpdatedEventImpl(
    override val id: String,

    @EventSourcing.Metadata
    override val companyId: String,

    @EventSourcing.Encrypted
    override val email: String,

    @EventSourcing.Encrypted
    override val name: String,

    @EventSourcing.Encrypted
    override val time: String
): UpdatedEvent